package cn.itcast.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends AbstractResponseMessage {

    private String from;
    private String content;

    public ChatResponseMessage(boolean success, String reason) {  //传递给父类AbstractResponseMessage, 发送失败时，传递给这个构造器
        super(success, reason);
    }

    public ChatResponseMessage(String from, String content) {   //发送成功时，采用这个构造器
        super(true, "发送成功");
        this.from = from;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return ChatResponseMessage;
    }
}
