package com.zhangsl.wallet.notify.listener.block;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.BTCFactory;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.common.BTCListenerNotifyBean;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import com.zhangsl.wallet.notify.common.NotifyConfirmApplication;
import com.zhangsl.wallet.notify.handle.WalletNotify;
import com.zhangsl.wallet.notify.listener.event.BlockHandle;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */
    public abstract class BaseBTCBlockHandle implements BlockHandle<ListenerNotifyBean>,BlockListener{

    private static final Logger logger = LoggerFactory.getLogger("listener");

    @Autowired
    private WalletNotify walletNotify;

    @Autowired
    private NotifyConfirmApplication notifyConfirmApplication;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TransactionRedis transactionRedis;

    @Override
    public void handleBlock(BigInteger blockNumber, Map<String, Object> params) {
        try {
            //高度信息判断
            Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), 0, blockNumber.doubleValue());
            if (null == result || result.size() == 0) {
                logger.error(FormatUtils.format("{0} transcation no data from {1} to {2}",getCoin(),0,blockNumber.doubleValue()));
            }
            result.forEach(tuple -> {
                try {
                    String jsonValue= tuple.getValue();
                    BTCListenerNotifyBean btcNotifyBean = JSONObject.parseObject(jsonValue,BTCListenerNotifyBean.class);
                    double block = tuple.getScore();
                    TxBean bean = null;
                    if (StringUtils.isEmpty(btcNotifyBean.getAddress())) {
                        bean = transactionRedis.getTransactoin(getCoin(), btcNotifyBean.getType(), btcNotifyBean.getTxId());
                    } else {
                        bean = transactionRedis.getTransactoinByAddress(getCoin(), btcNotifyBean.getAddress(), btcNotifyBean.getTxId());
                    }
                    if (bean != null) {
                        double confirm = blockNumber.doubleValue() - block + 1;
                        JSONObject jsonObject = BTCFactory.getBitCoinService(getCoin()).getTransaction(btcNotifyBean.getTxId());
                        JsonRpcHandler.checkException(jsonObject);
                        //交易的确认数信息
                        long confirmations = jsonObject.getJSONObject("result").getLong("confirmations");
                        if (confirmations > notifyConfirmApplication.getConfirm(getCoin()) && transactionRedis.existKey(FormatUtils.format(RedisContant.NOTIFY_C_T, getCoin(),btcNotifyBean.getType(), btcNotifyBean.getTxId()))) {
                            //最后的一个区块的确认数
                            stringRedisTemplate.opsForZSet().remove(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), jsonValue);
                        } else {
                            walletNotify.notify(bean, confirmations);
                        }
                        logger.info(FormatUtils.format("coin:{0},transcation:{1} confirmations:{2},confirm:{3}", getCoin(), btcNotifyBean.getTxId(), confirmations, confirm));
                    } else {
                        logger.warn(FormatUtils.format("{0} transcation {1} from redis is null ",getCoin(),btcNotifyBean.getTxId()));
                    }
                } catch (Exception e) {
                    //TODO 报警处理
                    logger.error("notify forEach error",e);
                }
            });

        } catch (Exception e) {
            logger.error("notify error",e);
        }
    }

    @Override
    public void handle(ListenerNotifyBean message, Map<String, Object> params) {
        double block = Double.valueOf(params.get("numberBlock").toString());
        transactionRedis.setValueByScore(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), JSONObject.toJSONString(message), block);
    }
}

