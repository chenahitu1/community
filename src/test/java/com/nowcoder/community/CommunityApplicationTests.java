package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);

        AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
        //这时如果我们有多个AlphaDao接口这时applicationContext.getBean(AlphaDao.class)
        //就不知道该要去用哪个  解决办法 我们可以使用@primary注释在你想要用的那个类上面
        //这种情况下也可以体现出为什么我们要先去创建一个接口 然后再去创建它的实现类
        //上面这种情况 比如我们想用myBaits的时候我们可以不用来改变test类的代码 因为我们要调用的都是同一个接口 减少代码之间的耦合度
        System.out.println(alphaDao.select());

        //我们可以通过指定bean的名称从而来获取指定的容器
        AlphaDao alphaHibernate = applicationContext.getBean("alphaHibernate", AlphaDao.class);
        System.out.println(alphaHibernate.select());
    }
    @Test
    public void testBeanManagement(){
        /*此处可以优化 没有使用调用接口的方式来调用*/
        //被容器管理的bean默认是单实例的
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);//这边打印出来的hashcode值和上面是一样的
    }
    @Test
    public void testBeanConfig() {
        /*这边获取bean实例为什么是这样写 和上面几个不一样*/
        SimpleDateFormat simpleDateFormat =
                applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }
    //以上都是我们主动去获取bean

    //下面我们使用依赖注入的方法 让接口类主动注入到容器中
    @Autowired
    @Qualifier("alphaHibernate")//因为有多个实现我们可以指定哪个bean注入到容器中
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Test
    public void testID(){
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
    }

    /*controller 调用service service调用dao*/
}
