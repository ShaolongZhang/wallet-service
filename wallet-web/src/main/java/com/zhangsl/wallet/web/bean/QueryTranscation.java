package com.zhangsl.wallet.web.bean;

import java.io.Serializable;

/**
 * Created by zhang.shaolong on 2018/3/10.
 */
public  class QueryTranscation implements Serializable {

    private String coinType; //币种信息

    private String transaction;

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

    @Override
    public String toString() {
        return "ThinkQueryTranscation{" +
                "coinType='" + coinType + '\'' +
                ", transaction='" + transaction + '\'' +
                '}';
    }
}
