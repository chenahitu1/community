package com.nowcoder.community.dao;

import com.nowcoder.community.enity.Comment;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface CommentMapper {

    //查询评论的实体
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);
    //查询评论的数量
    int selectCountByEntity(int entityType,int entityId);
    //添加评论数量
    int insertComment(Comment comment);

    //根据ID查
    Comment selectCommentById(int id);
}
