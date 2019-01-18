package com.zhangsl.wallet.common.bean;

public enum TxType {
    RECEIVE("receive"),
    SEND("send");

    private String type;

    TxType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
