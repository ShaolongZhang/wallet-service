package com.zhangsl.wallet.notify.listener.block;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
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

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhang.shaolong on 2018/4/9.
 */
public abstract class BaseETHBlockHandle<T> implements BlockHandle<T>,BlockListener {

    private static final Logger logger = LoggerFactory.getLogger("notify");

    @Autowired
    private WalletNotify walletNotify;

    @Resource
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    private NotifyConfirmApplication notifyConfirmApplication;

    @Autowired
    protected TransactionRedis transactionRedis;

    /**
     * 处理业务block信息
     * @param blockNumber
     * @param params
     */
    @Override
    public  void handleBlock(BigInteger blockNumber, Map<String,Object> params) {
        try {
            //高度信息判断
            Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), 0, blockNumber.doubleValue());
            if (null == result || result.size() == 0) {
                logger.error(FormatUtils.format("{0} transcation no data from {1} to {2}",getCoin(),0,blockNumber.doubleValue()));
            }
            result.forEach(tuple -> {
                String jsonValue= tuple.getValue();
                ListenerNotifyBean notifyBean = JSONObject.parseObject(jsonValue,ListenerNotifyBean.class);
                double block = tuple.getScore();
                try {
                    TxBean bean = transactionRedis.getTransactoin(getCoin(),notifyBean.getType(),notifyBean.getTxId());;
                    if (bean != null) {
                        double confirm = blockNumber.doubleValue() - block + 1;
                        if (confirm > notifyConfirmApplication.getConfirm(getCoin()) && transactionRedis.existKey(FormatUtils.format(RedisContant.NOTIFY_C_T,getCoin(),notifyBean.getType(),notifyBean.getTxId()))) {
                            stringRedisTemplate.opsForZSet().remove(FormatUtils.format(RedisContant.TRANSCATION_TYPE, getCoin()), jsonValue);
                        } else {
                            //最后的一个区块的确认数
                            walletNotify.notify(bean, confirm);
                        }
                    } else {
                        logger.warn(FormatUtils.format("{0} transcation {1} from redis is null ",getCoin(),notifyBean.getTxId()));
                    }
                } catch (Exception e) {
                    logger.error("notify forEach  error",e);
                }
            });

        } catch (Exception e) {
            logger.error("notify error",e);
        }
    }
}
