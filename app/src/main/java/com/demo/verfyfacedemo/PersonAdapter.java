package com.demo.verfyfacedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.demo.verfyfacedemo.gson.PersonBean;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends ArrayAdapter<PersonBean> {
    private int resourceId;

    public PersonAdapter(Context context, int textViewResourceId, ArrayList<PersonBean> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PersonBean personBean = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);

        TextView nameView = (TextView) view.findViewById(R.id.name_view);
        TextView idView = (TextView) view.findViewById(R.id.id_view);
        TextView genderView = (TextView) view.findViewById(R.id.gender_view);

        String gender = personBean.Gender == 1 ? "男" : "女";

        nameView.setText("姓名：" + personBean.PersonName);
        idView.setText("ID：" + String.valueOf(personBean.PersonId));
        genderView.setText("性别：" + gender);

        return view;
    }


}
