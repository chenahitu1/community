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
* MVC的底层原理
* 1.DispatcherServlet表示前端控制器，整个SpringMVC的控制中心。用户发出请求，DispatcherServlet接收请求并
* 拦截请求(DispatcherServlet会将url拆分成三部分 1.服务器域名http://localhost:8080 2.SpringMVC部署在服务器上面的web站点 SpringBoot上面就是tomcat 3.控制器Controler)
* 2.HandlerMapping为处理器映射。DispatcherServlet调用HandlerMapping,HandlerMapping根据请求url查找Handler
* 3.HandlerExecution表示具体的Handler,其主要作用是根据url查找控制器，如上url被查找控制器
* 4.HandlerExecution将解析后的信息传递给DispatchServlet,如解析控制器映射等
* 5.HandlerAdapter表示处理器适配，按照其特定的规则去执行Handler
* 6.Handler让具体的Controller执行
* 7.Controller将具体的执行信息返回HandlerAdapter,如ModelAndView(相当于我们要传数据给前端，包括我们要跳转到哪里)
* 8.HandlerAdapter将视图逻辑名或者模型传递给DispatcherServlet(给前端带一些数据，并且指定哪个前端)
* 9.DispatchServlet调用视图解析器（ViewResolver）来解析HandlerAdapter传递的逻辑视图名
* 10.视图解析器将解析的逻辑视图名传给DispatcherServlet
* 11.DispatcherServlet根据视图解析器的视图结果调用具体的视图
* 12.最终视图呈现给用户
*
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
