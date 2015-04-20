package com.example.zyh.autotakephoto;


public class UserInfo {

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
}
