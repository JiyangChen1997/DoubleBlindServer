package com.cjy.doubleblindserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.doubleblindserver.domain.Sig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/12/11 23:54
 */
@Mapper
public interface SignatureDao extends BaseMapper<Sig> {
}
