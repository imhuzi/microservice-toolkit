package com.uyibai.microservice.toolkit.generator;

import io.swagger.models.auth.In;

import java.io.Serializable;


public class UserInfo implements Serializable {
    private Integer id;
    private String name;
    private String avatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
