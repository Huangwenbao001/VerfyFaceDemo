package com.demo.verfyfacedemo.gson;

import android.icu.text.IDNA;

public class ErrorData {
    public Response Response;

    public class Response {
        public Error Error;

        //唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
        public String RequestId;
    }

    public class Error {
        public String Code;
        public String Message;
    }
}
