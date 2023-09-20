package plus.scg.microservice.toolkit.generator;


import java.util.List;
import java.util.Map;

public interface DemoService {

    String path(Integer age);

    String getName(String userId);

    UserInfo create(UserInfo userInfo);

    UserInfo batchCreate(List<UserInfo> users);
    void batchMap(Map<String, UserInfo> users);
}
