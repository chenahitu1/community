package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//单实例的时候容器会调用初始化方法 容器关闭时也会调用销毁方法
//但是多实例的时候不会去调用销毁方法 无论你创建了多少实例 它只负责创建 不负责销毁
//@Scope("prototype")//如果不想bean创建的实例是单实例 可以通过此注解来改变
public class AlphaService {

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
}
