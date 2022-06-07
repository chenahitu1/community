package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.enity.DiscussPost;
import com.nowcoder.community.enity.LoginTicket;
import com.nowcoder.community.enity.Message;
import com.nowcoder.community.enity.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectLetters(){
        List<Message> list=messageMapper.selectConversations(111,0,20);
        for(Message message :list){
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        messageMapper.selectLetters("111_112",0,10);
        for(Message message : list){
            System.out.println(message);
        }

        count=messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count=messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);

    }

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
        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(101, 0, 10,0);
        Iterator<DiscussPost> iterator = discussPostList.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        int discussPostRows = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(discussPostRows);

    }
    @Test
    public void testInsertPosts(){
        DiscussPost discussPost=new DiscussPost();
        discussPost.setUserId(800);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setType(1);
        discussPost.setContent("asda");
        discussPost.setCommentCount(5);
        discussPost.setTitle("asda");
        discussPost.setScore(95);
        discussPostMapper.insertDiscussPost(discussPost);

    }

    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);



    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
    @Test
    public void testUpdateLoginTicket(){
        loginTicketMapper.updateStatus("abc", 1);
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
}
