package com.demo.verfyfacedemo;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

public class LoadPress {
    private ProgressDialog progressDialog;

    public void showProgressDialog(BaseActivity activity){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog (){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
