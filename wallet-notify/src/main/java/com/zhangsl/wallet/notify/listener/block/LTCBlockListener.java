package com.zhangsl.wallet.notify.listener.block;

import com.zhangsl.wallet.common.coin.CoinType;
import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */
@Component("LTCBLOCK")
public class LTCBlockListener extends BaseBTCBlockHandle {

    @Override
    public String getCoin() {
        return CoinType.CoinBlockEnum.LTC.toString();
    }
}

