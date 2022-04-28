package com.nowcoder.community.service;

import com.nowcoder.community.enity.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //实现点赞功能
    public void like(int userId,int entityType,int entityId,int entityUserId){
//        //返回一个该类型like:entity:entityType:entityId的String
//        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//        //redisset集合中是否存在该类型 如果是表示已经点过赞  如果不是表示还没点赞
//        boolean isMember=redisTemplate.opsForSet().isMember(entityLikeKey,userId);
//        if(isMember){
//            //如果点过赞  则删除
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            //如果没有点过赞  则添加赞
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //返回一个该类型like:entity:entityType:entityId的String
                String entityLikeKey=RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                //返回一个该类型like:user:userId的String
                String userLikeKey=RedisKeyUtil.getUserLikeKey(entityUserId);

                //redisset集合中是否存在该类型 如果是表示已经点过赞  如果不是表示还没点赞
                boolean isMember=operations.opsForSet().isMember(entityLikeKey,userId);

                operations.multi();
                if(isMember){
                    //如果点过赞  则删除
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //如果点过赞  则减少一个赞
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    //如果没有点过赞  则添加赞
                    operations.opsForSet().add(entityLikeKey,userId);
                    //如果没有点过赞 则增加一个赞
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }
    //查询实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey=RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey=RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }
    //查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey=RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }
}
