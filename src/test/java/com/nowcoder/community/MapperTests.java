package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.enity.DiscussPost;
import com.nowcoder.community.enity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user=userMapper.selectByName("liubei");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder11@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){

        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcode.com/101.png");
        user.setCreateTime(new Date());

        int rows =userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdateUser(){
        int rows=userMapper.updateStatus(150,1);
        System.out.println(rows);
        rows=userMapper.updateHeader(150,"http://www.nowcode.com/102.png");
        System.out.println(rows);
        rows=userMapper.updatePassword(150,"789123");
        System.out.println(rows);
    }
    @Autowired
    DiscussPostMapper discussPostMapper;
    //每次写完mapper.xml文件时测试一下有没有写错 因为那边的出错率比较高
    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(101, 0, 10);
        Iterator<DiscussPost> iterator = discussPostList.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        int discussPostRows = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(discussPostRows);
    }
}
