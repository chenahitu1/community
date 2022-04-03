package com.nowcoder.community.dao;

import com.nowcoder.community.enity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //根据id查询user
    User selectById(int id);

    //根据用户名查user
    User selectByName(String username);

    //根据email查user
    User selectByEmail(String email);

    //插入用户
    int insertUser(User user);

    //更新状态
    int updateStatus(int id,int status);

    //更新图像路径
    int updateHeader(int id,String headerUrl);

    //更新密码
    int updatePassword(int id,String password);
}
