package com.zhangsl.wallet.web.bean;

import java.io.Serializable;

/**
 * Created by zhang.shaolong on 2018/3/10.
 */
public  class ThinkQueryBalance implements Serializable {

    private String coinType; //币种信息

    private String address;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ThinkQueryBalance{" +
                "coinType='" + coinType + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
