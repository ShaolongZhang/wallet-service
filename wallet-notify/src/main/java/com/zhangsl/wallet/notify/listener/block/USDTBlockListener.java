package com.zhangsl.wallet.notify.listener.block;

import com.zhangsl.wallet.common.coin.CoinType;
import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/9.
 */
@Component("USDTBLOCK")
public class USDTBlockListener extends BaseETHLogBlockHandle{

    @Override
    public String getCoin() {
        return CoinType.CoinEnum.USDT.toString();
    }
}
