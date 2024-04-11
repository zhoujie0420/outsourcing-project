package com.lhqjlb.project.config;

import cn.dev33.satoken.exception.SaTokenException;
import com.lhqjlb.project.util.JsonUtil;
import com.lhqjlb.project.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public R handlerBindException(BindException e) {
        BindingResult result = e.getBindingResult();
        if (result.hasFieldErrors()) {
            String collect = result.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(";"));
            return R.error(collect);
        }
        return R.error("参数错误");
    }

    @ExceptionHandler(ResultException.class)
    public R handlerCustomException(ResultException e) {
        return e.getR();
    }

    @ExceptionHandler(Throwable.class)
    public R handlerAll(Throwable e) {
        log.error(e.getMessage(), e);
        R error = R.error(R.ResultEnum.INTERNAL_SERVER_ERROR);
        log.info(JsonUtil.toJsonStr(error));
        return error;
    }

    @ExceptionHandler(SaTokenException.class)
    public R handlerException(SaTokenException e) {
        log.error(e.getMessage());
        return R.error(R.ResultEnum.UNAUTHORIZED);
    }
}
