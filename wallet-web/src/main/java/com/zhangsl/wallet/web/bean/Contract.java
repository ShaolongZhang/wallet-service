package com.zhangsl.wallet.web.bean;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 合约发送
 * Created by zhang.shaolong on 2018/4/28.
 */
public class Contract {

    private String from; //发送合约的地址

    private BigInteger gasLimit; //gas信息

    private String data;

    private BigDecimal gasPrice; //矿工费用

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public BigDecimal getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigDecimal gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public String toString() {
        return "ThinkContract{" +
                "from='" + from + '\'' +
                ", gasLimit=" + gasLimit +
                ", data='" + data + '\'' +
                ", gasPrice=" + gasPrice +
                '}';
    }
}
