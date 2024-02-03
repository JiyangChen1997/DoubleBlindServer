package com.cjy.doubleblindserver.common.baseclass.tool;





import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 15:14
 */
public class PageListUtil {

    public static List pageList(Integer current, Integer size, List vos) {
        List<List> lists = Lists.partition(vos,size);
        List pageRecords = new ArrayList();
        if (lists.size() > current-1)
            pageRecords = lists.get(current-1);
        return pageRecords;
    }
}
