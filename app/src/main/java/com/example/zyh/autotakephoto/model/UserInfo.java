package com.example.zyh.autotakephoto.model;


import android.content.Context;
import android.content.SharedPreferences;

public class UserInfo {

    private static final String SPNAME = "user";

    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "userId";

    private static UserInfo userInfo = new UserInfo();

    private String name;
    private String userId;

    private UserInfo() {}

    public static UserInfo getUserInfo() { return userInfo; }

    public synchronized void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public synchronized void setUserId(String userId) { this.userId = userId; }
    public String getUserId() { return userId; }

    /**
     * 从sharedPreference中读取数据。
     * 数据不应该从activity中读取，MVC模型！
     */
    public static void initUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        String name = sp.getString(UserInfo.NAME, "");
        String id = sp.getString(UserInfo.USER_ID, "");
        getUserInfo().name = name;
        getUserInfo().userId = id;
    }

    public synchronized static void storageInSharedPreference(Context context, String name, String userId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(UserInfo.SPNAME, Context.MODE_PRIVATE).edit();
        editor.putString(UserInfo.NAME, name);
        editor.putString(UserInfo.USER_ID, userId);
        editor.apply();
        getUserInfo().name = name;
        getUserInfo().userId = userId;
    }

    /**
     * 注销用户
     */
    public static void destroyUserInfo(Context context) {
        storageInSharedPreference(context, "", "");
    }
}
