package com.cjy.doubleblindserver.common.baseclass.fieldquerysort.SO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 14:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSo {

    @NotNull(message = "当前页")
    private Integer current = 1;

    @NotNull(message = "页条数")
    private Integer size = 10;
}
