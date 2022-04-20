package com.uyibai.microservice.toolkit.generator;

import com.uyibai.microservice.common.api.ResData;

import java.util.List;
import java.util.Map;

public interface DemoService {

    String path(Integer age);

    String getName(String userId);

    UserInfo create(UserInfo userInfo);

    UserInfo batchCreate(List<UserInfo> users);
    ResData<UserInfo> batchArray(UserInfo[] users);
    void batchMap(Map<String, UserInfo> users);
}
