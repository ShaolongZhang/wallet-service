package com.zhangsl.wallet.notify.listener.coin;

import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import org.springframework.stereotype.Component;


/**
 * Created by zhang.shaolong on 2018/3/16.
 */
@Component("USDT")
public class USDTListener extends BaseETHTokenListner {

    public USDTListener() {
        super();
    }

    @Override
    protected String getContracts() {
        return EthToken.USDT_TOKEN.getContract();
    }

    @Override
    protected CoinType.CoinBlockEnum getBlock() {
        return CoinType.CoinBlockEnum.USDT;
    }
}
