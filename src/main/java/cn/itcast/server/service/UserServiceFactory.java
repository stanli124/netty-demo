package cn.itcast.server.service;

public abstract class UserServiceFactory {

    //这里是工厂模式吗？ 比较像单例模式
    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
