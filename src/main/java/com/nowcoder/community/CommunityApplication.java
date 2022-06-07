package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
/*MVC
* 三层架构  表现层、业务层、数据访问层
* 表现层里面 分为模型层Model  View视图层 Controller 控制层 称为MVC
* 表现层中的Controller负责网页解析（这个时候需要依赖业务层service service需要依赖数据层dao）
* 然后返回给model形成一个模型  最终交给View对页面进行渲染 以view的形式返回给客户端
*
* SpringMVC的核心组件 前端控制器 DispatcherServlet  需要理解SpringMVC的底层
* */
/*MyBatis
* 核心组件
* SqlSessionFactory:用于创建SqlSession的工厂类
* SqlSession:MyBatis的核心组件，用于向数据库执行SQL
* 主配置文件：xml配置文件，可以对mybatis的底层行为做出详细的配置
* Mapper接口：就是Dao接口，在MyBatis中习惯性的称之为Mapper.
* Mapper映射器：用于编写SQL,并将SQL和实体类映射组件，采用xml,注解均可实现
* */


@SpringBootApplication//会自动扫描配置类所在的包以及它的子包 也就是com.nowcoder.community包下的所有包
//该类本身就是一个配置类
public class CommunityApplication {


    @PostConstruct
    public void init(){
        //解决netty自动冲突问题
        //see netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        //CommunityApplication就是一个配置文件
        SpringApplication.run(CommunityApplication.class, args);
    }

}
