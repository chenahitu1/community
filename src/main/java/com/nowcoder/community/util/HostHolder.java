package com.nowcoder.community.util;

import com.nowcoder.community.enity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象
 */
@Component
public class HostHolder {
    /**
     * 为保证多个线程对共享变量的安全访问，
     * 通常会使用synchronized来保证同一时刻只有一个线程对共享变量进行操作。
     * 这种情况下可以将类变量放到ThreadLocal类型的对象中，使变量在每个线程中都有独立拷贝，
     * 不会出现一个线程读取变量时而被另一个线程修改的现象。最常见的ThreadLocal使用场景为用来解决数据库连接、Session管理等
     */
    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    //避免数据太多占用内存
    public void clear(){
        users.remove();
    }
}
