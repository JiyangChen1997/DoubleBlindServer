package com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 14:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueryCondition {

    private String field;

    private String operator;

    private Object value;
}
