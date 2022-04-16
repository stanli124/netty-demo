package cn.itcast.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends Message {

    private Set<String> members;
    private String groupname;

    public GroupMembersResponseMessage(Set<String> members, String groupname) {
        this.members = members;
        this.groupname = groupname;
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
