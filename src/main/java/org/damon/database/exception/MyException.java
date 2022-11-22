package org.damon.database.exception;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.damon.database.enums.ResponseEnum;
import org.damon.database.util.ResponseVO;

/**
 * @Author Damon
 * @Date 2020/11/24 18:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MyException extends Exception  {

    private static final long serialVersionUID = -3996538053463991442L;

    private final ResponseEnum responseEnum;

    private final ResponseVO<String> responseVO;

    public MyException(ResponseEnum responseEnum, Throwable cause) {
        super(responseEnum.getMessage(), cause);
        log.error(cause.getMessage(), cause);
        this.responseEnum = responseEnum;
        responseVO = ResponseVO.error(responseEnum);
    }

    public MyException(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
        responseVO = ResponseVO.error(responseEnum);
    }

    public MyException(Integer code, Object obj) {
        responseEnum = null;
        this.responseVO = ResponseVO.error(code, JSONUtil.toJsonStr(obj));
    }

    public MyException(Integer code, String errmsg) {
        responseEnum = null;
        this.responseVO = ResponseVO.error(code, errmsg);
    }

}
