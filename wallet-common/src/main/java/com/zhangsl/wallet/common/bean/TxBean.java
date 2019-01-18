package com.zhangsl.wallet.common.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 用来通知给第三方信息
 * Created by zhang.shaolong on 2018/4/6.
 */
public class TxBean implements Serializable {

    private String address;

    private String coin;

    private BigDecimal amount;

    private BigDecimal gas;

    private BigInteger blockNumber;

    private String type;

    private String transaction;

    private long createTime;

    private int confirm;

    /* transaction#coin#type#amount#confirm#key */
    private String sign;


    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }



    @Override
    public String toString() {
        return "TxBean{" +
                "address='" + address + '\'' +
                ", coin='" + coin + '\'' +
                ", amount=" + amount +
                ", gas=" + gas +
                ", blockNumber=" + blockNumber +
                ", type='" + type + '\'' +
                ", transaction='" + transaction + '\'' +
                ", createTime=" + createTime +
                ", confirm=" + confirm +
                '}';
    }
}
