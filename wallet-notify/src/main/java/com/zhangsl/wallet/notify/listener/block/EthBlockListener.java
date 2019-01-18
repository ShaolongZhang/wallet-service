package com.zhangsl.wallet.notify.listener.block;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/3/16.
 */
@Component("ETHBLOCK")
public class EthBlockListener extends BaseETHBlockHandle<ListenerNotifyBean> {

    private static final Logger logger = LoggerFactory.getLogger(EthBlockListener.class);


    @Override
    public String getCoin() {
        return CoinType.CoinEnum.ETH.toString();
    }

    //记录区块的交易信息
    @Override
    public void handle(ListenerNotifyBean message, Map<String, Object> params) {
        if (message == null) {
            return;
        }
        double bigInteger = Double.valueOf(params.get("numberBlock").toString());
        transactionRedis.setValueByScore(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), JSONObject.toJSONString(message),bigInteger);
    }

}
