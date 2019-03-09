package com.web.cloudapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="usertable")
public class User {

    @Id
    @Column(name="username",nullable = false)
    private String userName;

    @Column(name = "password",nullable = false)
    private String password;

    public User(){

    }

    public User(String userName, String password) {
        this.userName = userName ;
        this.password = password;
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

    @Override
    public String toString() {
        return userName;
    }
}
