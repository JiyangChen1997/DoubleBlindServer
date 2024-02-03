package com.cjy.doubleblindserver.common.baseinterface;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.doubleblindserver.common.baseclass.customresponse.CustomException;
import com.cjy.doubleblindserver.common.baseclass.customresponse.ResponseCode;

import java.io.Serializable;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 21:59
 */
public interface IInsertUpDelService<T> {

    default boolean verifyIdExist(Serializable id, BaseMapper mapper) {
        return mapper.selectById(id) != null;
    }

    default boolean verifyIdExist(Serializable id, IService service) {
        return service.getById(id) != null;
    }

    default void verifyId(Serializable id, BaseMapper mapper) {
        if (!verifyIdExist(id, mapper))
            throw new CustomException(ResponseCode.OBJECT_NOT_EXISTS);
    }

    default void verifyId(Serializable id, IService service) {
        if (!verifyIdExist(id, service))
            throw new CustomException(ResponseCode.OBJECT_NOT_EXISTS);
    }
}
