package cn.itcast.message;

import lombok.Data;
import lombok.ToString;

@Data //原来是这个类加了@Data注解，所有Java代码中不需要生成getters and setters，而在编译的时候会自动生成getters and setters
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;


    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public String getPassword() {
//        return username;
//    }

}
