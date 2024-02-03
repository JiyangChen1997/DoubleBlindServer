package com.cjy.doubleblindserver.common.baseinterface;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.QueryStructure;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.PageSo;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.QueryCondition;
import com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO.SortCondition;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 11:06
 */
public interface IViewService<T> extends IService<T> {
    default T detail(Serializable id){
        return getById(id);
    }

    default IPage<T> pageList(List<QueryCondition> queryConditions, List<SortCondition> sortConditions, PageSo pageSo) {
        QueryWrapper queryWrapper = new QueryStructure().wheresByList(queryConditions).orderBysByList(sortConditions).build();
        Page<T> page = new Page<>(pageSo.getCurrent(), pageSo.getSize());
        return page(page, queryWrapper);
    }

    default List<T> list(List<QueryCondition> queryConditions, List<SortCondition> sortConditions) {
        QueryWrapper queryWrapper = new QueryStructure().wheresByList(queryConditions).orderBysByList(sortConditions).build();
        return list(queryWrapper);
    }
}
