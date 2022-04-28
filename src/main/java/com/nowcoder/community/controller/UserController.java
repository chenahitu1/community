package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.enity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("user")
public class UserController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    //上传路径
    @Value("${community.path.upload}")
    private String uploadPath;

    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    //优化：页面应该给一个修改成功的提示
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String fileName=headerImage.getOriginalFilename();
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //优化：这边只判断了文件是否有后缀 但是没有判断是不是图片
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名 优化：这边文件明用时间戳的方式来命名比较不会重复
        fileName= CommunityUtil.generateUUID()+suffix;

        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+fileName);
        //将图片放入该路径 也就是存储文件
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);

        }
        //更新当前用户头像路径(web访问路径)
        //localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName=uploadPath+"/"+fileName;
        //文件后缀 为什么这边要加1
        String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os=response.getOutputStream();
                ){
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/modifyPassword",method = RequestMethod.POST)
    public String modifyPassword(String originalPassword,String newPassword,String ackPassword,Model model){
        if(originalPassword==null){
            model.addAttribute("error1","原密码不能为空");
            return "/site/setting";
        }
        if(newPassword==null){
            model.addAttribute("error2","新密码不能为空");
            return "/site/setting";
        }
        if(ackPassword==null){
            model.addAttribute("error3","确认密码不能为空");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        String salt = user.getSalt();
        String password=CommunityUtil.md5(originalPassword+salt);
        if(!password.equals(user.getPassword())){
            model.addAttribute("error1","原密码不正确");
            return "/site/setting";
        }
        if(!ackPassword.equals(newPassword)){
            model.addAttribute("error3","两次输入密码不一致");
            return "/site/setting";
        }
        userService.modifyPassword(user.getId(),password);
        System.out.println("修改成功");
        return "redirect:/index";
    }
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    //个人主页
    @RequestMapping(path="/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user= userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量 存在问题 不能即时更新
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        System.out.println(likeCount);

        //关注数量
        long followeeCount=followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount=followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);

        //是否已经关注
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null){
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);

        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";

    }

}
