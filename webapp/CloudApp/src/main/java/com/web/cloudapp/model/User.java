package com.web.cloudapp.model;

public class User {

    String userName;
    String password;

    public User(){

    }

    public User(String userName, String password) {
        userName = this.userName;
        password = this.password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
