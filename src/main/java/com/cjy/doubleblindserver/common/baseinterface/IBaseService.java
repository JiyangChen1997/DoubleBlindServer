package com.cjy.doubleblindserver.common.baseinterface;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.PageSo;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.PageSo;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.QueryCondition;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.SortCondition;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/21 21:16
 */
public interface IBaseService<T> extends IInsertUpDelService<T>, IService<T> {

    void add(T dto);

    void delete(List<Serializable> ids);

    void update(T dto);

    default T detail(Serializable id) {
        return fillForeign(getById(id));
    }

    IPage<T> pageList(List<QueryCondition> queryConditions, List<SortCondition> sortConditions, PageSo pageSo);

    default T fillForeign(T dto, String... excludeKeys) {
        return dto;
    }

    default List<T> fillForeignList(List<T> dtos, String... excludeKeys) {
        return dtos;
    }
}
