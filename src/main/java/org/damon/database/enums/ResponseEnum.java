package org.damon.database.enums;

/**
 * @Author Damon
 * @Date 2020/11/18 10:54
 */
public enum ResponseEnum {

    /**
     * 0 表示返回成功
     */
    SUCCESS(0,"成功"),

    ACCESS_TOKEN_INVALID(1001,"access_token无效"),

    INSUFFICIENT_PERMISSIONS(1003,"该用户权限不足以访问该资源接口"),

    UNAUTHORIZED(1004,"访问此资源需要完全的身份验证"),

    FALL_BACK_INFO(2000,"服务不可用"),

    TIMEOUT(3001,"服务超时"),

    INCORRECT_PARAMS(4000, "参数不正确"),

    FAILD(4100,"操作失败"),

    SYS_PARAM_MISS(4200,"系统参数丢失"),

    RECORD_ALREADY_UPDATE(4300,"记录已被修改，请重新提交");

    private final Integer code;
    private final String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
