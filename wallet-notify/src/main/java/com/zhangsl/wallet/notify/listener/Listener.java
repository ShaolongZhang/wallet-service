package com.zhangsl.wallet.notify.listener;

import java.math.BigInteger;

/**
 * Created by zhang.shaolong on 2018/4/5.
 */
public interface Listener {

    /**
     * 启动服务监听
     *
     * @return
     */
    boolean start();

    /**
     * 设置去要监听的区块信息
     *
     * @param startBlock
     * @param endStartBlock
     * @return
     */
    boolean start(BigInteger startBlock, BigInteger endStartBlock);

}
