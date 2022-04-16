package cn.itcast.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupJoinRequestMessage extends Message {
    private String groupName;
    private String username;
//    private String name = null;

    public GroupJoinRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

//    public GroupJoinRequestMessage(String name, String username, String groupName) {
//        this.groupName = groupName;
//        this.username = username;
//        this.name = name;
//    }

    @Override
    public int getMessageType() {
        return GroupJoinRequestMessage;
    }
}
