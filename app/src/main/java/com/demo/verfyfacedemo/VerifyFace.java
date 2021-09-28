package com.demo.verfyfacedemo;

//人脸核身

import android.app.ProgressDialog;
import android.telecom.Call;

import com.google.gson.Gson;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

//import static com.demo.verfyfacedemo.VerifyFace.resquestPostData;


public class VerifyFace {


    private static String SecretId  = "AKIDCCxReaFdkbBVYKoxpQknZ4IEuJRItCEz";
    private static String SecretKey = "cx1oKZXEeohJdhV2HFwMVNzJM8etrXx6";

    private static String Url       = "https://iai.tencentcloudapi.com";
    //规范请求串
    private static String  HTTPRequestMethod     = "POST";
    private static String  CanonicalURI          = "/";
    private static String  CanonicalQueryString  = "";
    private static String  CanonicalHeaders      = "content-type:application/json; charset=utf-8\nhost:iai.tencentcloudapi.com\n";
    private static String  SignedHeaders         = "content-type;host";//参与签名的头部信息

    //签名字符串
    private static String  Algorithm             = "TC3-HMAC-SHA256";
    private static String  Service               = "iai";
    private static String  Stop                  = "tc3_request";

    //版本
    public static String  Version               = "2020-03-03";
    //接入地域
    public static String  Region                = "ap-guangzhou";


    /***
     * 示例身份信息认证请求方法
     * @param
     */
    public void begin(String action, Map<String, String> datas, HttpCallbackListener listener) {
        Map<String, Object> params = new HashMap<>();

        for (Map.Entry<String, String> entry : datas.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            params.put(key, value);
        }

        if (action.equals("CreatePerson")) {
            String gender = (String) params.get("Gender");
            params.remove("Gender");
            if (gender.equals("1")) {
                params.put("Gender",1);
            } else {
                params.put("Gender",2);
            }
        }

        Gson gson = new Gson();
        String param = gson.toJson(params);
        //发送请求 本地封装的https 请求

        this.getAuthTC3(action, param, Version, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                listener.onFinish(response);
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    /**
     * v3鉴权
     * @param action  方法名
     * @param paramJson json化的参数
     * @param version  版本号 2018-03-01
     * @return
     */
    public void getAuthTC3(String action, String paramJson, String version, HttpCallbackListener listener){
        try{
            String hashedRequestPayload   =   HashEncryption(paramJson);
            String CanonicalRequest =
                    HTTPRequestMethod + '\n' +
                            CanonicalURI + '\n' +
                            CanonicalQueryString + '\n' +
                            CanonicalHeaders + '\n' +
                            SignedHeaders + '\n' +
                            hashedRequestPayload;
            //时间戳
            Date date = new Date();
            //微秒->秒
            String timestamp    =   String.valueOf(date.getTime() / 1000);

            //格林威治时间转化
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String dateString = formatter.format(date.getTime());

            //签名字符串
            String credentialScope = dateString + "/" + Service + "/" + Stop;
            String hashedCanonicalRequest =   HashEncryption(CanonicalRequest);
            String stringToSign           =   Algorithm + "\n" +
                    timestamp + "\n" +
                    credentialScope + "\n" +
                    hashedCanonicalRequest;

            //计算签名
            byte[] secretDate             =   HashHmacSha256Encryption(("TC3" + SecretKey).getBytes("UTF-8"), dateString);
            byte[] secretService          =   HashHmacSha256Encryption(secretDate, Service);
            byte[] secretSigning          =   HashHmacSha256Encryption(secretService, Stop);

            //签名字符串
            byte[] signatureHmacSHA256    =   HashHmacSha256Encryption(secretSigning, stringToSign);

            StringBuilder builder = new StringBuilder();
            for (byte b : signatureHmacSHA256) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                builder.append(hex);
            }
            String signature = builder.toString().toLowerCase();
            //组装签名字符串
            String authorization          =   Algorithm + ' ' +
                    "Credential=" + SecretId + '/' + credentialScope + ", " +
                    "SignedHeaders=" + SignedHeaders + ", " +
                    "Signature=" + signature;

            //创建header 头部
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", authorization);
            headers.put("Host", "iai.tencentcloudapi.com");
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("X-TC-Action", action);
            headers.put("X-TC-Version", version);
            headers.put("X-TC-Timestamp", timestamp);
            headers.put("X-TC-Region", Region);
            //request 请求

            this.resquestPostData(Url, paramJson, headers, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    listener.onFinish(response);
                }

                @Override
                public void onError(Exception e) {
                    listener.onError(e);
                }
            });

        }catch(Exception e){
            listener.onError(e);
        }
    }

    /*
     * Function  :   发送Post请求到服务器
     * Param     :   params请求体内容，encode编码格式
     */
    private void resquestPostData(String strUrlPath, String data, Map<String, String> headers, HttpCallbackListener listener) {

        try {
            URL url = new URL(strUrlPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);            //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");          //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
          //  httpURLConnection.setReadTimeout(200);
            //设置header
            if (headers.isEmpty()) {
                //设置请求体的类型是文本类型
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            } else {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    httpURLConnection.setRequestProperty(key, value);
                }
            }
            //设置请求体的长度
        //    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length()));

            //获得输出流，向服务器写入数据
            if (data != null) {

                // 设置文件长度
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
            }
            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                if (listener != null) {
                    listener.onFinish(dealResponseResult(inptStream));
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }


    /**
     *
     */
    private static String  HashEncryption(String s)  throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(s.getBytes());
        //替换java DatatypeConverter.printHexBinary(d).toLowerCase()
        StringBuilder builder = new StringBuilder();
        for (byte b : sha.digest()) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }
        return builder.toString().toLowerCase();
    }

    private static byte[] HashHmacSha256Encryption(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes("UTF-8"));
    }

    public interface HttpCallbackListener {
        void onFinish(String response);
        void onError(Exception e);
    }

}
