package com.zhangsl.wallet.common;

import java.io.Serializable;

/**
 * 接口统一的返回信息
 *
 * Created by zhang.shaolong
 */
public class Result<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 描述信息
     */
    private String message;

    /**
     * 结果信息
     */
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
