package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BisCodeEnum;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    /**
     * 处理数据校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleException(MethodArgumentNotValidException e){
        log.error(e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(item -> {
            errorMap.put(item.getField(), item.getDefaultMessage());
        });
        return R.error(BisCodeEnum.VALID_EXCEPTION.getCode(), BisCodeEnum.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

    /**
     * 处理全局异常
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error(throwable.getMessage());
        return R.error(BisCodeEnum.UNKOWN_EXCEPTION.getCode(), BisCodeEnum.UNKOWN_EXCEPTION.getMsg());
    }

}
