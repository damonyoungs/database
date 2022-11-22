package org.damon.database.handle;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.damon.database.exception.MyException;
import org.damon.database.enums.ResponseEnum;
import org.damon.database.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

/**
 * @Author chengrong.yang
 * @Date 2020/11/20 9:57
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandle {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseVO<Object> validExceptionHandler(BindException e) {
        log.error(e.getMessage(),e);
        return getResponseVO(e.getBindingResult());

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseVO<String> runTimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(),e);
        return ResponseVO.error(ResponseEnum.FAILD.getCode(), e.getMessage());

    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseVO<Object> exceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(),e);
        return getResponseVO(e.getBindingResult());
    }

    @ExceptionHandler(value = MyException.class)
    public ResponseVO<String> exceptionHandler(MyException e){
        log.error(JSONUtil.toJsonStr(e));
        return e.getResponseVO();
    }

    private ResponseVO<Object> getResponseVO(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        String errorMsg = ResponseEnum.INCORRECT_PARAMS.getMessage();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Locale currentLocale = LocaleContextHolder.getLocale();
        for (FieldError fieldError : fieldErrors) {
            String errorMessage = messageSource.getMessage(fieldError, currentLocale);
            msg.append(fieldError.getField()).append("：").append(errorMessage).append(",");
            errorMsg = errorMessage;
        }
        String res = new String(msg);
        res = res.substring(0, res.length() - 1);
        return new ResponseVO<>(ResponseEnum.INCORRECT_PARAMS.getCode(), errorMsg, res);

    }
}
