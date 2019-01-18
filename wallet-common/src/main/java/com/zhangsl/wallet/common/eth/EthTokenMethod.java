package com.zhangsl.wallet.common.eth;

/**
 * Created by zhang.shaolong on 2018/3/19.
 */
public enum EthTokenMethod {

    TOKEN_BALANCE("balanceOf"),

    TOKEN_TRANSFER("transfer"),

    TOKEN_TRANSFER_EVENT("Transfer"),

    TOKEN_NAME("name"),

    TOKEN_SYMBOL("symbol"),

    TOKEN_DECIMALS("decimals"),

    //transferFrom
    TOKEN_TRANSFER_FROM("transferFrom");

     //方法名称
    private String method;

    EthTokenMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
