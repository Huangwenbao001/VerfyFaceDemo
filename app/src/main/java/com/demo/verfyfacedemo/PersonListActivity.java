package com.demo.verfyfacedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.verfyfacedemo.gson.PersonBean;
import com.demo.verfyfacedemo.gson.Persons;
import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PersonListActivity extends BaseActivity {


    private VerifyFace verifyFace = new VerifyFace();
    private Persons persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        initViews();
        loadList();

    }

    private void initViews () {
        TextView titleView = (TextView) findViewById(R.id.title_textView);
        titleView.setText("人员库");

    }

    private void loadList () {

        Map<String, String> datas = new HashMap<>();

        datas.put("GroupId", "5718350099");

        LoadPress loadPress = new LoadPress();

        loadPress.showProgressDialog(PersonListActivity.this);

        verifyFace.begin("GetPersonList", datas, new VerifyFace.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                persons = new Gson().fromJson(response, Persons.class);

                ArrayList<PersonBean> list;
                list = persons.Response.PersonInfos;
                PersonAdapter adapter = new PersonAdapter(PersonListActivity.this, R.layout.person_item, list);

                ListView listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(adapter);

                loadPress.closeProgressDialog();
            }

            @Override
            public void onError(Exception e) {

            }
        });



    }
}