package cn.itcast.message;

import lombok.Data;

@Data
public class QuitRequestMessage {
    String username;

    public QuitRequestMessage(String username) {
        this.username = username;
    }
}
