package com.demo.verfyfacedemo.gson;

public class CreatePerson {

    public Response Response;

    public class Response {
        //人脸图片唯一标识。
        public String FaceId;
        //疑似同一人的PersonId。
        //当 UniquePersonControl 参数不为0且人员库中有疑似的同一人，此参数才有意义。
        public String SimilarPersonId;
        //人脸识别所用的算法模型版本。
        public String FaceModelVersion;
        //唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
        public String RequestId;
    }


}
