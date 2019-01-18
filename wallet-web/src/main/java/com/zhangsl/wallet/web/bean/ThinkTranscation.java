package com.zhangsl.wallet.web.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by zhang.shaolong on 2018/3/10.
 */
public  class ThinkTranscation implements Serializable {

    private String coinType; //币种信息

    private String from; //发送地址信息

    private String to;  //需要发送的信息

    private BigDecimal amount; //转帐金额

    private BigDecimal gas; //矿工费用

    private long userId;

    private String sign;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getGas() {
        return gas;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    @Override
    public String toString() {
        return "ThinkTranscation{" +
                "coinType='" + coinType + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount=" + amount +
                ", gas=" + gas +
                ", userId=" + userId +
                ", sign='" + sign + '\'' +
                '}';
    }
}

