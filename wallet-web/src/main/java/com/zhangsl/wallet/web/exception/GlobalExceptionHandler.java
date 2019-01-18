package com.zhangsl.wallet.web.exception;

import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.Result;
import com.zhangsl.wallet.common.ResultBuilder;
import com.zhangsl.wallet.common.exception.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 错误信息的统一处理
 *
 * Created by zhang.shaolong on 2018/4/7.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger("error");


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> jsonErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("thinkbit error ",e);
        if (e instanceof WalletException) {
            return ResultBuilder.buildFailedResult(((WalletException) e).getErrorCode(), e.getMessage());
        }
        return ResultBuilder.buildFailedResult(ErrorCode.SERVICE_ERROR.getCode(), ErrorCode.SERVICE_ERROR.getDescription());
    }
}
