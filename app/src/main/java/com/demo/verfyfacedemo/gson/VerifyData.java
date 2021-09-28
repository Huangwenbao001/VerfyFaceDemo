package com.demo.verfyfacedemo.gson;

public class VerifyData {
    public Response Response;

    public class Response {
        //若需要验证两张图片中人脸是否为同一人，3.0版本误识率千分之一对应分数为40分，误识率万分
        // 之一对应分数为50分，误识率十万分之一对应分数为60分。 一般超过50分则可认定为同一人
        public float Score;
        //是否为同一人的判断。
        public boolean IsMatch;
        //人脸识别所用的算法模型版本。
        public String FaceModelVersion;
        //唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
        public String RequestId;
    }
}
