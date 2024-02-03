package com.cjy.doubleblindserver.common.baseclass.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 14:00
 */
@Component
public class MyMetaObjectHandler  implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        boolean hasSetter = metaObject.hasSetter("updateTime");
        if (hasSetter) {
            this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        boolean hasSetter = metaObject.hasSetter("updateTime");
        if (hasSetter) {
            this.strictUpdateFill(metaObject,"updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
}
