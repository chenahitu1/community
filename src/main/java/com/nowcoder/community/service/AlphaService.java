package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.enity.DiscussPost;
import com.nowcoder.community.enity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.crypto.Data;
import java.util.Date;

@Service
//单实例的时候容器会调用初始化方法 容器关闭时也会调用销毁方法
//但是多实例的时候不会去调用销毁方法 无论你创建了多少实例 它只负责创建 不负责销毁
//@Scope("prototype")//如果不想bean创建的实例是单实例 可以通过此注解来改变
public class AlphaService {

    private static final Logger logger= LoggerFactory.getLogger(AlphaService.class);

    //service 依赖dao
    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct//在调用初始化方法之前调用该方法
    public void init(){
        System.out.println("初始化AlphaService");
    }


    @PreDestroy//表示销毁之前调用该方法
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 为什么要写事务  为了防止数据出现脏读幻读和不可重复读
     * 利用事务的四种隔离级别来解决数据的读问题
     * 读未提交
     * 读提交
     * 可重复读
     * 串行化
     * @return
     */
    //声明式事务管理  开发中大多使用这种情况
    //REQUIRED:支持当前事务（外部事务），如果不存在则创建新事务
    //REQUIRES NEW:创建一个新事物，并且暂停当前事务（外部事物）
    //NESTED:如果当前存在事务（外部事物），则嵌套在该事务中执行(独立提交和回滚)，否则就会和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user =new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";

    }
    @Autowired
    private TransactionTemplate  transactionTemplate;
    //编程式事务管理
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user =new User();
                user.setUsername("alpha1");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("1234"+user.getSalt()));
                user.setEmail("alpha1@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/98t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post=new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("hello1");
                post.setContent("新人报道1");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
    //该注解的意思是让该方法在多线程的环境下，被异步调用
    @Async
    public void execute1(){
        logger.debug("execute1");
    }

    //只要程序中有线程跑着它就一定会执行
//    @Scheduled(initialDelay = 10000,fixedDelay = 1000)
    public void execute(){
        logger.debug("execute2");
    }

}
