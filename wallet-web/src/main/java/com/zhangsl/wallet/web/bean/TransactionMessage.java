package com.zhangsl.wallet.web.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易签名的基础信息
 * Created by zhang.shaolong on 2018/3/12.
 */
public class TransactionMessage implements Serializable {

    private String from;

    private String to;

    private String transaction;

    private BigDecimal amout; //金额信息

    private BigDecimal gasPrice; //费用

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmout() {
        return amout;
    }

    public void setAmout(BigDecimal amout) {
        this.amout = amout;
    }

    public BigDecimal getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigDecimal gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "TransactionMessage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", transaction='" + transaction + '\'' +
                ", amout='" + amout + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                '}';
    }
}
