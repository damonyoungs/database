package top.damon.database.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import top.damon.database.enums.ResponseEnum;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * @Author Damon
 * @Date 2020/11/18 16:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 3530232874785067977L;
    /**
     * 异常码
     */
    private Integer code;

    /**
     * 描述
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public ResponseVO(Integer code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public ResponseVO(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }

    public ResponseVO(ResponseEnum responseEnum, T data) {
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
        this.data = data;
    }

    public static ResponseVO<String> success() {
        return new ResponseVO<>(ResponseEnum.SUCCESS);
    }

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(ResponseEnum.SUCCESS, data);
    }

    public static <T> ResponseVO<T> success(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static ResponseVO<String> error(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static ResponseVO<String> error(ResponseEnum responseEnum) {
        return new ResponseVO<>(responseEnum);
    }

    public static <T> ResponseVO<T> error(ResponseEnum responseEnum, T data) {
        return new ResponseVO<>(responseEnum, data);
    }

    public static ResponseVO<String> errorParams(String msg) {
        return new ResponseVO<>(ResponseEnum.INCORRECT_PARAMS.getCode(), msg);
    }

    public static ResponseVO<String> error(BindingResult result, MessageSource messageSource) {
        StringBuilder msg = new StringBuilder();
        //获取错误字段集合
        List<FieldError> fieldErrors = result.getFieldErrors();
        //获取本地locale,zh_CN
        Locale currentLocale = LocaleContextHolder.getLocale();
        //遍历错误字段获取错误消息
        for (FieldError fieldError : fieldErrors) {
            //获取错误信息
            String errorMessage = messageSource.getMessage(fieldError, currentLocale);
            //添加到错误消息集合内
            msg.append(fieldError.getField()).append("：").append(errorMessage).append(" , ");
        }
        return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS, msg.toString());
    }
}