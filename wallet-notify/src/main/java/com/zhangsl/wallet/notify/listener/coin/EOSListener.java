package com.zhangsl.wallet.notify.listener.coin;

import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import org.springframework.stereotype.Component;


/**
 * Created by zhang.shaolong on 2018/3/16.
 */
@Component("EOS")
public class EOSListener extends BaseETHTokenListner {

    public EOSListener() {
         super();
    }

    @Override
    protected String getContracts() {
        return EthToken.EOS_TOKEN.getContract();
    }

    @Override
    protected CoinType.CoinBlockEnum getBlock() {
        return CoinType.CoinBlockEnum.EOS;
    }

}
