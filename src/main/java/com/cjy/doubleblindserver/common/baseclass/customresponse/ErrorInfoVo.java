package com.cjy.doubleblindserver.common.baseclass.customresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 11:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfoVo {

    private String operateName;

    private String info;
}
