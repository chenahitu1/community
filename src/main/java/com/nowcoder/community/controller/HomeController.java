package com.nowcoder.community.controller;

import com.nowcoder.community.enity.DiscussPost;
import com.nowcoder.community.enity.Page;
import com.nowcoder.community.enity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderModel",defaultValue = "0") int orderModel){

        //方法调用前，SpringMVC会自动实例化Model和page,并将Page注入Model
        //所以，在thymeleaf中可以直接访问对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setLimit(10);
        page.setPath("/index?orderModel="+orderModel);

        List<DiscussPost> list= discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderModel);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list !=null){
            for(DiscussPost post: list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user= userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderModel",orderModel);
        return "/index";
    }
    @RequestMapping(path="/error",method=RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    @RequestMapping(path = "/denied",method=RequestMethod.GET)
    public String getDeniedPage(){
        return "error/404";
    }

}
