package com.nowcoder.community.dao;

import com.nowcoder.community.enity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

   List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderModel);

   //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //插入讨论内容
    int insertDiscussPost(DiscussPost discussPost);
    //查询评论
    DiscussPost selectDiscussPostById(int id);

    //更新评论数量
    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);

    int updateStatus(int id,int status);

    int updateScore(int id, double score);
}
