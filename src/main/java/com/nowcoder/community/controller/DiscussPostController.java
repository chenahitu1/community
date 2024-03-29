package com.nowcoder.community.controller;

import com.nowcoder.community.enity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user=hostHolder.getUser();
        //这边原来前端还没有设置的时候需要判断  但是现在前端已经设置成只有登录了才有我要发布的功能 所以判断不判断没影响
        if(user==null){
            return CommunityUtil.getJSONString(403,"你还没登录");
        }
        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发帖子事件
        Event event=new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey=RedisKeyUtil.getPostScoreKey();
        //为了去重 所以这边使用set存储
        redisTemplate.opsForSet().add(redisKey,post.getId());

        //报错情况后续统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }
    @Autowired
    private UserService userService;


    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post=discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user=userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //点赞数量
        long likeCount= likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);
        //点赞 状态
        int likeStatus=hostHolder.getUser()==null?0:
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        //获取评论集合
        //评论：给帖子评论
        //回复：给评论的评论

        //查评论列表
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //评论的Vo列表
        List<Map<String,Object>> commentVoList=new ArrayList<>();
        if(commentList!=null){
            for(Comment comment:commentList){
                //评论VO
                Map<String,Object> commentVo=new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount =likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //点赞的状态
                likeStatus=hostHolder.getUser()==null?0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //回复列表 这边不做分页处理
                List<Comment> replyList=commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String,Object>> replyVoList=new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply : replyList){
                        Map<String,Object> replyVo=new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        //点赞数量
                        likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus=hostHolder.getUser()==null?0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        //将replyVo装到集合里面
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);


            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }

    //置顶
    @RequestMapping(path="/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        User user=hostHolder.getUser();
        discussPostService.updateType(id,1);

        //触发发帖事件
        Event event=new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    //加精
    @RequestMapping(path="/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id,1);

        //触发发帖事件
        Event event=new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey=RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        return CommunityUtil.getJSONString(0);
    }

    //删除
    @RequestMapping(path="/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id,2);

        //触发发帖事件
        Event event=new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
