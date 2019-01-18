package com.zhangsl.wallet.notify.listener.event;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/3/10.
 */
public interface BlockHandle<T> {

    /**
     * 处理业务block信息
     * @param blockNumber
     * @param params
     */
    void handleBlock(BigInteger blockNumber, Map<String, Object> params);


    /**
     * 处理业务transction信息
     * @param message
     * @param params
     */
    void handle(T message, Map<String, Object> params);

}
