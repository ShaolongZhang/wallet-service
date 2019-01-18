package com.zhangsl.wallet.common.exception;


import com.zhangsl.wallet.common.ErrorCode;

/**
 * Created by zhang.shaolong
 */
public class WalletException extends RuntimeException {

    /** 错误码信息  **/
    private int errorCode;

    public WalletException() {
        super();
    }

    public WalletException(String message) {
        super(message);
        this.errorCode = ErrorCode.OTHER_ERROR.getCode();
    }

    public WalletException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode.getCode();
    }


    public WalletException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }


    public WalletException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

}
