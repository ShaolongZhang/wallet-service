package com.zhangsl.wallet.notify.common;

import java.io.Serializable;

/**
 * Created by zhang.shaolong on 2018/4/9.
 */
public class ListenerNotifyBean implements Serializable{

    private String type;

    private String txId;

    public ListenerNotifyBean(String type, String txId) {
        this.type = type;
        this.txId = txId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
