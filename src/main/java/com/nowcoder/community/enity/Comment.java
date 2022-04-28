package com.nowcoder.community.enity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Date;


public class Comment {
    private int id;
    private int userId;
    //设置评论的类型  用一张表解决所有的评论问题 1代表帖子 2代表评论 3代表用户 4代表题 5代表课程
    private int entityType;
    //实体的id 也就是这条帖子的id
    private int entityId;
    //评论指向谁
    private int targetId;
    //评论的内容
    private String content;
    private int status;
    private Date CreateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", CreateTime=" + CreateTime +
                '}';
    }
}
