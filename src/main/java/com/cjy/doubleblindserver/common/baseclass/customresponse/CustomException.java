package com.cjy.doubleblindserver.common.baseclass.customresponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 11:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CustomException extends RuntimeException{
    int code;
    String msg;
    Object data;

    public CustomException(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public CustomException(ResponseCode code) {
        this.msg = code.getMsg();
        this.code = code.getCode();
    }

    public CustomException(ResponseCode code, Object data) {
        this.msg = code.getMsg();
        this.code = code.getCode();
        this.data = data;
    }
}
