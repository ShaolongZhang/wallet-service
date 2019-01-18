package com.zhangsl.wallet.common;


/**
 * 主要是为了构建result 返回值信息
 *
 *  Created by zhang.shaolong
 */
public class ResultBuilder {


    /**
     * 返回系统错误信息
     *
     * @return
     */
    public static <T> Result<T> buildFailedResult() {
        return buildResult(500, "error", null);
    }

    /**
     * 返回指定描述的错误码信息
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> buildFailedResult(int code, String message) {
        return buildResult( code, message, null);
    }

    /**
     * 返回成功的信息
     *
     * @param data
     * @return
     */
    public static <T> Result<T> buildSuccessResult(T data) {
        return buildResult(200, "success", data);
    }


    /**
     * 构造返回结果信息
     * @param message
     * @param code
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Result<T> buildResult(int code, String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setResult(data);
        return result;
    }



}
