package com.zhangsl.wallet.web.bean;

import java.io.Serializable;

/**
 * Created by zhang.shaolong on 2018/3/10.
 */
public  class SendTranscation implements Serializable {

    private String coinType; //币种信息

    private String transaction;

    private String sign;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ThinkSendTranscation{" +
                "coinType='" + coinType + '\'' +
                ", transaction='" + transaction + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

}
