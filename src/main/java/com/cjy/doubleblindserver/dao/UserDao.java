package com.cjy.doubleblindserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.doubleblindserver.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/27 22:42
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
}
