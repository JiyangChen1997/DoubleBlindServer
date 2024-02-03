package com.cjy.doubleblindserver.common.baseinterface;

import com.cjy.doubleblindserver.common.baseclass.customresponse.ServerResponse;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/21 21:14
 */
public interface IBaseController<T> {
    ServerResponse add(T dto);

    ServerResponse delete(List<Serializable> ids);

    ServerResponse update(T dto);

    ServerResponse detail(Serializable id);

//    ServerResponse pageList(QuerySortConditionPageSo querySortConditionPageSo);
}
