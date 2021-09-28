package com.demo.verfyfacedemo;

import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ToastObj {

    public void show(AppCompatActivity activity, String content) {
        Toast toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
