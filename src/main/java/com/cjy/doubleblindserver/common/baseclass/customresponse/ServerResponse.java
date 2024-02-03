package com.cjy.doubleblindserver.common.baseclass.customresponse;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 11:20
 */
@Data
public class ServerResponse<T> implements Serializable {
    private int code;
    private String msg;

    @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
    private T data;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    public ServerResponse() {}

    private ServerResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = new Date();
    }

    private ServerResponse(int code, String msg) {
        this(code, msg, null);
    }

    public static <T> ServerResponse<T> success() {
        return new ServerResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg());
    }

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), data);
    }


    public static <T> ServerResponse<T> error() {
        return new ServerResponse(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
    }


    public static <T> ServerResponse<T> error(T data) {
        return new ServerResponse(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg(), data);
    }

    public static <T> ServerResponse<T> errorByCodeMsg(int code, String msg) {
        return new ServerResponse(code, msg);
    }

    public static <T> ServerResponse<T> errorByCode(ResponseCode code) {
        return new ServerResponse(code.getCode(), code.getMsg());
    }

    public static <T> ServerResponse<T> errorByCodeMsgData(int code, String msg, T data) {
        return new ServerResponse(code, msg, data);
    }
}
