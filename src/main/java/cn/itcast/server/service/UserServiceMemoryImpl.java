package cn.itcast.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//将所有用户的账号和密码都保存在内存中
public class UserServiceMemoryImpl implements UserService {
    private Map<String, String> allUserMap = new ConcurrentHashMap<>(); //为了保证线程安全，使用了并发HashMap

    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("zhaoliu", "123");
        allUserMap.put("qianqi", "123");
    }

    @Override
    public boolean login(String username, String password) {  //如果内存中有该用户，就返回true
        String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }

    @Override
    public boolean userExist(String username) {
        return allUserMap.get(username)!=null ? true : false;
    }
}
