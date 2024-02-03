package com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO;

import com.cjy.doubleblindserver.common.baseclass.validator.group.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 14:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QuerySortConditionPageSo {

    private List<QueryCondition> queryConditions;

    private List<SortCondition> sortConditions;

    @NotNull(message = "请输入分页", groups = Default.class)
    private PageSo pageSo;
}
