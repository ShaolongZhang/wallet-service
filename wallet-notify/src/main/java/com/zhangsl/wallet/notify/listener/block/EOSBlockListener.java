package com.zhangsl.wallet.notify.listener.block;

import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/9.
 */
@Component("EOSBLOCK")
public class EOSBlockListener extends BaseETHLogBlockHandle{

    @Override
    public String getCoin() {
        return CoinType.CoinEnum.EOS.toString();
    }
}
