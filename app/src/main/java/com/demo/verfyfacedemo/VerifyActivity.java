package com.demo.verfyfacedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.demo.verfyfacedemo.gson.CreatePerson;
import com.demo.verfyfacedemo.gson.ErrorData;
import com.demo.verfyfacedemo.gson.VerifyData;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class VerifyActivity extends BaseActivity {

    private VerifyFace verifyFace = new VerifyFace();
    private EditText idInput;
    private Button faceBtn;
    private String imageView;

    private LoadPress loadPress;
    private final int SPLASH_DISPLAY_LENGHT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        loadPress = new LoadPress();

        TextView titleView = (TextView) findViewById(R.id.title_textView);
        titleView.setText("验证");

        idInput = (EditText) findViewById(R.id.id_editText);
        //默认直接显示数字键盘
        idInput.setInputType(InputType.TYPE_CLASS_TEXT);
        idInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        Button createBtn = (Button) findViewById(R.id.button_create);
        faceBtn = (Button) findViewById(R.id.face_button1);

        // 编辑按钮
        Button editBtn = (Button) findViewById(R.id.edit_button);
        editBtn.setVisibility(View.VISIBLE);
        editBtn.setText("人员库");

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        faceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开照相机拍照选取
                ActivityCompat.requestPermissions(VerifyActivity.this,new String[] {
                        Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
                },1);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        // 人员库列表
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyActivity.this, PersonListActivity.class);

                startActivity(intent);
            }
        });
    }

    private void verify () {

        String idString = idInput.getText().toString();

        if (idString.length()< 1) {
            toastObj.show(VerifyActivity.this,"请输入ID");
            return;
        } else if (imageView.length() < 3) {
            toastObj.show(VerifyActivity.this,"请添加照片");
            return;
        } else if (isContainChinese(idString)) {
            toastObj.show(VerifyActivity.this, "人员ID不可含有中文（仅支持字母与数字）");
            return;
        }

        loadPress.showProgressDialog(VerifyActivity.this);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, String> datas = new HashMap<>();

                datas.put("Image", imageView);
                datas.put("PersonId", idInput.getText().toString());

                verifyFace.begin("VerifyFace", datas, new VerifyFace.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        VerifyData person = new Gson().fromJson(response,VerifyData.class);
                        loadPress.closeProgressDialog();

                        ToastObj toastObj = new ToastObj();
                        if (person.Response.IsMatch) {
                            toastObj.show(VerifyActivity.this,"验证通过");
                        } else {
                            if (response.contains("Score")) {
                                toastObj.show(VerifyActivity.this,"验证不通过，非同一个人，相似度：" + String.valueOf(person.Response.Score));
                            } else {
                                ErrorData error1 = new Gson().fromJson(response, ErrorData.class);
                                toastObj.show(VerifyActivity.this, error1.Response.Error.Message);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        },SPLASH_DISPLAY_LENGHT);





    }
    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    //处理返回结果的函数，下面是隐示Intent的返回结果的处理方式，具体见以前我所发的intent讲解
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri mImageCaptureUri = data.getData();
            if (mImageCaptureUri == null && data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    imageView = bitmapToBase64(bitmap);
                    faceBtn.setBackground(drawable);
                }
            }
        }
    }


    /*
     * bitmap转base64
     * */
    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}