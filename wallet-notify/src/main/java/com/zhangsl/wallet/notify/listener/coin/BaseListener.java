package com.zhangsl.wallet.notify.listener.coin;


import com.zhangsl.wallet.notify.listener.Listener;
import com.zhangsl.wallet.notify.listener.event.BlockService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */
public abstract  class BaseListener implements Listener {

    @Autowired
    protected BlockService blockService;

}
