package cn.itcast.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupSessionMemoryImpl implements GroupSession {
    //也是将聊天室存放在内存中，为了保证线程安全，使用了ConcurrentHashMap
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();


    @Override
    public boolean isExistGroup(String groupName) {
        if (groupMap.containsKey(groupName)){
            return true;
        }
        return false;
    }

    /*
        根据组名和组成员创建群聊
         */
    @Override
    public Group createGroup(String name, Set<String> members) {
        Group group = new Group(name, members);
        return groupMap.putIfAbsent(name, group);
    }

    /*
    根据组名，如果存在就添加新成员
     */
    @Override
    public Group joinMember(String name, String member) {
        //computeIfPresent函数 ： 如果指定键的值存在且非空，则会尝试在给定键及其当前映射值的情况下计算新映射
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().add(member); //在组的成员Set集合中添加新成员
            return value; //返回添加了新成员Group
        });
    }

    /*
    移除组员
    返回移除后的Group
     */
    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }

    /*
    移除聊天组
     */
    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }

    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }

    @Override
    public List<Channel> getMembersChannel(String name) {
        return getMembers(name).stream()
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
