package com.example.zyh.autotakephoto.persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class Storage{

    private String preferencesName;
    private SharedPreferences.Editor editor;
    private Context context;

    public Storage(Context context, String name) {
        preferencesName = name;
        if (TextUtils.isEmpty(preferencesName)) {
            throw new NullPointerException();
        }
        this.context = context;
        editor = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE).edit();
    }



}
