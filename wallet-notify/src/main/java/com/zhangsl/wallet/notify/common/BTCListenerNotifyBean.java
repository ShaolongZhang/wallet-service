package com.zhangsl.wallet.notify.common;

/**
 * Created by zhang.shaolong on 2018/5/14.
 */
public class BTCListenerNotifyBean  extends ListenerNotifyBean {

    private String address;

    public BTCListenerNotifyBean(String type, String txId) {
        super(type, txId);
    }

    public BTCListenerNotifyBean(String type, String txId,String address) {
        super(type, txId);

        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
