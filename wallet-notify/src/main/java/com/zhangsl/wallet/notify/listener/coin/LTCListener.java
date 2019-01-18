package com.zhangsl.wallet.notify.listener.coin;

import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.Rediskey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/5.
 */
@Component("LTC")
public class LTCListener extends BaseBTCListener {

    private static final Logger logger = LoggerFactory.getLogger(BTCListener.class);

    public LTCListener(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    public String getWalletNotifyKey() {
        return Rediskey.LTC_WALLET_NOTIFY_KEY;
    }

    @Override
    public String getBlockNotifyKey() {
        return Rediskey.LTC_NOTIFY_KEY;
    }

    @Override
    public CoinType.CoinBlockEnum getBlock() {
        return CoinType.CoinBlockEnum.LTC;
    }

}
