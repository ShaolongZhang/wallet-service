package com.zhangsl.wallet.notify.redis;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhang.shaolong on 2018/4/16.
 */
@Component
public class TransactionRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加一个交易信息
     */
    public  void addTransactoin(TxBean txBean) {
        if (txBean == null) {
            return;
        }
        if (StringUtils.isEmpty(txBean.getCoin()) || StringUtils.isEmpty(txBean.getTransaction())) {
            return;
        }
        //一天过期时间
        stringRedisTemplate.opsForValue().set(FormatUtils.format(RedisContant.TYPE_TRANSCATION, txBean.getCoin(),txBean.getTransaction()), JSONObject.toJSONString(txBean));
        stringRedisTemplate.expire(FormatUtils.format(RedisContant.TYPE_TRANSCATION, txBean.getCoin(),txBean.getTransaction()),1, TimeUnit.DAYS);

    }


    public long hasTxid(String coin,String txId) {
        if(StringUtils.isEmpty(txId)) {
            return -1L;
        }
        Long value = stringRedisTemplate.opsForZSet().rank(FormatUtils.format(RedisContant.SENT_TYPE_TRANSCATION,coin),txId);
        if (value == null) {
            return -1L;
        }
        return value.longValue();
    }

    public long deleteTxid(String coin,String txId) {
        if(StringUtils.isEmpty(txId)) {
            return 0L;
        }
        Long value = stringRedisTemplate.opsForZSet().remove(FormatUtils.format(RedisContant.SENT_TYPE_TRANSCATION,coin),txId);
        stringRedisTemplate.delete(FormatUtils.format(RedisContant.TRANSCATION_SIGN,txId));
        if (value == null) {
            return 0L;
        }
        return value.longValue();
    }

    /**
     *
     * 直接存储key-value
     */
    public void setValue(String key,String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 直接存储key-value
     */
    public void setValue(String key,String value,long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.expire(key, timeout,unit);
    }

    /**
     * 直接存储key-value
     */
    public void setValueByScore(String key,String value,double score) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        stringRedisTemplate.opsForZSet().add(key, value,score);
    }

    /**
     * 获取key值信息
     */
    public String getValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean existKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return true;
        }
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 获取key值信息
     */
    public Object getHashValue(String key,String hashKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
            return null;
        }
        return stringRedisTemplate.boundHashOps(key).get(hashKey);
    }


    /**
     * 添加一个交易信息
     */
    public  void addTransactoinByType(TxBean txBean) {
        if (txBean == null) {
            return;
        }
        if (StringUtils.isEmpty(txBean.getCoin()) || StringUtils.isEmpty(txBean.getTransaction()) || StringUtils.isEmpty(txBean.getType())) {
            return;
        }
        //一天过期时间
        stringRedisTemplate.opsForValue().set(FormatUtils.format(RedisContant.TYPE_TRANSCATION_TYPE, txBean.getCoin(),txBean.getType(),txBean.getTransaction()), JSONObject.toJSONString(txBean));
        stringRedisTemplate.expire(FormatUtils.format(RedisContant.TYPE_TRANSCATION_TYPE, txBean.getCoin(),txBean.getType(),txBean.getTransaction()),1, TimeUnit.DAYS);
    }

    /**
     * 添加一个交易信息
     */
    public  void addTransactoinByAddress(TxBean txBean) {
        if (txBean == null) {
            return;
        }
        if (StringUtils.isEmpty(txBean.getCoin()) || StringUtils.isEmpty(txBean.getTransaction()) || StringUtils.isEmpty(txBean.getAddress())) {
            return;
        }
        //一天过期时间
        stringRedisTemplate.opsForValue().set(FormatUtils.format(RedisContant.TYPE_TRANSCATION_ADDESS, txBean.getCoin(),txBean.getAddress(),txBean.getTransaction()), JSONObject.toJSONString(txBean));
        stringRedisTemplate.expire(FormatUtils.format(RedisContant.TYPE_TRANSCATION_ADDESS, txBean.getCoin(),txBean.getAddress(),txBean.getTransaction()),1, TimeUnit.DAYS);
    }

    /**
     * 获取一个交易信息,根据type类型
     */
    public  TxBean getTransactoinByAddress(String coin,String address,String tx) {
        if (StringUtils.isEmpty(coin) || StringUtils.isEmpty(tx)) {
            return null;
        }
        String txjson = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.TYPE_TRANSCATION_ADDESS, coin, address,tx));
        TxBean bean = JSONObject.parseObject(txjson, TxBean.class);
        return bean;
    }

    /**
     * 获取一个交易信息,根据type类型
     */
    public  TxBean getTransactoin(String coin,String type,String tx) {
        if (StringUtils.isEmpty(coin) || StringUtils.isEmpty(tx)) {
            return null;
        }
        String txjson = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.TYPE_TRANSCATION_TYPE, coin, type,tx));
        TxBean bean = JSONObject.parseObject(txjson, TxBean.class);
        return bean;
    }

    /**
     * 获取一个交易信息
     */
    public  TxBean getTransactoin(String coin,String tx) {
        if (StringUtils.isEmpty(coin) || StringUtils.isEmpty(tx)) {
            return null;
        }
        String txjson = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.TYPE_TRANSCATION, coin, tx));
        TxBean bean = JSONObject.parseObject(txjson, TxBean.class);
        return bean;
    }

    /**
     * 判断地址是否是我们的地址
     *
     * @param address
     * @return
     */
    public boolean isMyAddress(String coin,String address) {
        if (StringUtils.isEmpty(coin) || StringUtils.isEmpty(address)) {
            return false;
        }
        return stringRedisTemplate.hasKey(FormatUtils.format(RedisContant.ADDRESS_TYPE,coin,address));
    }

}
