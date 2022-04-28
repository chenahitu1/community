package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    //controller依赖service
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    //mvc
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration= request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println("*************************");
        System.out.println(request.getParameter("code"));

        //返回响应数据
//        response.setCharacterEncoding("utf-8");//作用是指定服务器响应给浏览器的编码 不指定的话使用iso-8859-1
        response.setContentType("text/html;charset=utf-8");//作用是指定服务器响应给浏览器的编码。同时，浏览器也是根据这个参数来对其接收到的数据进行重新编码（或者称为解码）
        try (
                PrintWriter writer = response.getWriter();
                ){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET请求 默认请求就是GET请求


    //students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    //括号里面的参数也可以不用写注解 但是参数名要和和请求时的参数名一致 写注解是为了更好的对这个参数进行描述
    //name表示名字，required=false表示没有这个参数也可以，defaultValue表示参数没写时的默认值
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    ///student/123
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    //@PathVariable注解作用是获取路径中的变量
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //get传的数据量有限
    //post请求 请求表单中的数据
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应html数据 也就是返回一个动态页面

    //方式一 model和view同时返回
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav=new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        //该视图在thymeleaf里面
        mav.setViewName("/demo/view");
        return mav;
    }

    //方式二  方式二和方式一的效果是一样的 方式二先把model封装进容器里面 再返回view
    //方式二更加简洁一些
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";
    }


    //像浏览器响应json数据  异步请求的时候需要响应json数据
    //java对象--> json--> js对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return emp;
    }

    //当我们需要响应多组数据的时候
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> emp1=new HashMap<>();
        emp1.put("name","李四");
        emp1.put("age",32);
        emp1.put("salary",9000.00);
        Map<String,Object> emp2=new HashMap<>();
        emp2.put("name","王五");
        emp2.put("age",25);
        emp2.put("salary",6000.00);
        Map<String,Object> emp3=new HashMap<>();
        emp3.put("name","张三");
        emp3.put("age",23);
        emp3.put("salary",8000.00);
        list.add(emp1);
        list.add(emp2);
        list.add(emp3);
        return list;
    }
    //cookie示例
    @RequestMapping(path = "/cookie/set",method=RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie对象 每个cookie对象只能存放单个key和value  cookie只能是字符串  因为他是要传回给客户端的 太大会影响传输
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie的生效范围
        cookie.setPath("/community/alpha");
        //设置cookie的生存时间
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }
    //session 优点 数据存在服务端安全 但是服务端存在压力
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        //session因为是存在服务端的 所以它可以存放任何数据
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(@SessionAttribute("id") int id){
        System.out.println(id);
        return "get session";
    }

    //ajax示例
    @RequestMapping(path="/ajax",method=RequestMethod.POST)
    @ResponseBody
    public String textAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功！");
    }

}
