package com.zhangsl.wallet.notify.handle;


import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.common.util.JsonUtils;
import com.zhangsl.wallet.common.util.SHAUtils;
import com.zhangsl.wallet.notify.common.NotifyConfirmApplication;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

//通知接口信息
@Component
public class WalletNotify {

    private static final Logger logger = LoggerFactory.getLogger("notify");
    
    @Autowired
    protected TransactionRedis transactionRedis;

    @Autowired
    private NotifyConfirmApplication notifyConfirmApplication;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Value("${wallet.notify.key}")
    private String key ;

    @Value("${wallet.notify.url}")
    private String walletNotiry;

    public void notify(TxBean bean, double confirm) {
        if (bean == null) {
            return;
        }
        bean.setConfirm(Double.valueOf(confirm).intValue());
        setSing(bean);
        String messge = JsonUtils.toJSON(bean);
        logger.info("notify-->wallet service:"+messge);
        try {
            //TODO 通知到其他到的业务数据
            setConfirm(bean);
        } catch (Exception e) {
            logger.error("notify error "+ messge);
        }
    }

    public void notify(TxBean bean) {
        if (bean == null) {
            return;
        }
        setSing(bean);
        String messge = JsonUtils.toJSON(bean);
        logger.info("notify-->wallet service:"+messge);
        try {
            //TODO 通知到其他到的业务数据
            setConfirm(bean);
        } catch (Exception e) {
            logger.error("notify error "+messge,e);
        }
    }


    public void setConfirm(TxBean bean){
        if (bean.getConfirm() >= notifyConfirmApplication.getConfirm(bean.getCoin())) {
            transactionRedis.setValue(FormatUtils.format(RedisContant.NOTIFY_C_T, bean.getCoin(), bean.getType(), bean.getTransaction()), String.valueOf(bean.getConfirm()), 1, TimeUnit.HOURS);
        }
    }


    public TxBean buildNotify(String addres, String coin, BigDecimal amount, BigDecimal gas, String type, String transcation, BigInteger blockNumber) {
        TxBean bean = new TxBean();
        bean.setAddress(addres);
        bean.setCoin(coin);
        bean.setAmount(amount);
        bean.setGas(gas);
        bean.setTransaction(transcation);
        bean.setType(type);
        bean.setCreateTime(System.currentTimeMillis());
        bean.setConfirm(0);
        bean.setBlockNumber(blockNumber);
        return bean;
    }


    private void setSing(TxBean bean) {
            /* transaction#coin#type#confirm#key */
        StringBuilder sb = new StringBuilder(bean.getTransaction()).append("#").append(bean.getCoin())
                .append("#").append(bean.getType())
                .append("#").append(bean.getConfirm())
                .append("#").append(key);
        String sign = SHAUtils.getSHA256(sb.toString());
        bean.setSign(sign);
    }
}
