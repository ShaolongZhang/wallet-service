package com.zhangsl.wallet.monitor.redis;

import java.io.Serializable;

/**
 * Created by zhang.shaolong on 2018/5/27.
 */
public class MonitorPub implements Serializable {

    private MonitorEnum monitor;

    private String message;

    public MonitorPub(MonitorEnum monitor, String message) {
        this.monitor = monitor;
        this.message = message;
    }

    public MonitorEnum getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorEnum monitor) {
        this.monitor = monitor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MonitorPub{" +
                "monitor=" + monitor +
                ", message='" + message + '\'' +
                '}';
    }
}
