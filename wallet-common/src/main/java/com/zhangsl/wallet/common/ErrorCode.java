package com.zhangsl.wallet.common;

/**
 * Created by zhang.shaolong on 2018/4/4.
 */
public enum ErrorCode {
    SERVICE_ERROR(500, "service error"),
    BALCNACE_ERROR(504,"balance error"),
    PARAM_ERROR(505, "params error"),
    SIGN_ERROR(506, "sign error"),
    OTHER_ERROR(507,"query balance error"),
    TRANSCATION_ERROR(508,"transcation error"),
    ADDRESS_ERROR(509,"address error"),
    ETH_ENOUGH_ERROR(510,"eth enough error");



    private int code;
    private String description;

    private ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {

        return description;
    }
}
