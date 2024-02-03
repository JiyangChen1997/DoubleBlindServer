package com.cjy.doubleblindserver.common.baseclass.customresponse;

public enum ResponseCode {

    SYSTEM_ERROE(-1, "服务器出错，请联系开发人员"),
    ILLEGAL_REQUEST(400, "不合法的请求"),

    SUCCESS(0, "成功"),
    ERROR(-1, "失败"),
    OBJECT_EXISTS(-1, "记录已存在"),
    OBJECT_NOT_EXISTS(-1, "记录不存在"),

    USER_NOT_EXIST(-1, "用户不存在"),
    NO_PERMISSION(-1, "权限不足"),
    USERNAME_OR_PASSWORD_ERROR(-1,"用户名或密码错误"),
    USERNAME_EXIST(-1,"用户名已存在"),

    TOKEN_VERIFY_FAIL(-2, "token验证失败，请重新登录"),
    TOKEN_INVALID(-2, "token不合法"),
    TOKEN_OVERDUE(-2, "token已过期"),
    NEED_LOGIN(-2, "请登录"),

    QRCODE_INVALID(-1, "二维码无效"),
    QRCODE_OVERDUE(-1, "二维码已过期"),

    NEED_MAC(-1,"请输入mac"),
    NEED_T(-1,"请输入t"),

    REDIS_SET_ERROR(-1,"redis插入失败"),
    FORM_INFORMATION_INCOMPLETE(-1,"表单信息未完成");


    private final int code;
    private final String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
