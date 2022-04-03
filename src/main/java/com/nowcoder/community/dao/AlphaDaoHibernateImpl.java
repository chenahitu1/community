package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

//访问数据库 使用@Repository 接口 将组件放到容器中 便于管理对象
@Repository("alphaHibernate")//每个bean都有名字 默认是类名 我们也可以在写注解的时候写入设置名字
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
