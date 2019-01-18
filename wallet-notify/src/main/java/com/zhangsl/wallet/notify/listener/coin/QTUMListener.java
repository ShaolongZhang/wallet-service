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
@Component("QTUM")
public class QTUMListener extends BaseBTCListener {

    private static final Logger logger = LoggerFactory.getLogger(QTUMListener.class);

    public QTUMListener(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    public String getWalletNotifyKey() {
        return Rediskey.QTUM_WALLET_NOTIFY_KEY;
    }

    @Override
    public String getBlockNotifyKey() {
        return Rediskey.QTUM_NOTIFY_KEY;
    }

    @Override
    public CoinType.CoinBlockEnum getBlock() {
        return CoinType.CoinBlockEnum.QTUM;
    }


}
