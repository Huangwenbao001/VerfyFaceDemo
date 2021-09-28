package com.demo.verfyfacedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.verfyfacedemo.gson.PersonBean;
import com.demo.verfyfacedemo.gson.Persons;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // 状态栏导航栏设置
        // 隐藏导航栏
        this.getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        TextView titleView = (TextView) findViewById(R.id.title_textView);
        titleView.setText("首页");
        Button backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setVisibility(View.GONE);

        Button createBtn = (Button) findViewById(R.id.button_create_personal);
        Button listBtn = (Button) findViewById(R.id.button_list_personal);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPersonal();
            }
        });
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPersonal();
            }
        });

        Button verifyBtn = (Button) findViewById(R.id.button_verify_personal);


        // 验证
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VerifyActivity.class);
                startActivity(intent);
            }
        });
    }

    // 创建人员
    private void createPersonal () {
        Intent intent = new Intent(MainActivity.this, CreatePersonalActivity.class);
        startActivity(intent);
    }

    // 人员库
    private void listPersonal () {

        Intent intent = new Intent(MainActivity.this, PersonListActivity.class);

        startActivity(intent);

    }
}