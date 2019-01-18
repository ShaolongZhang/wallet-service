package com.zhangsl.wallet.web.redis;

import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/19.
 */
@Component
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public void setTxid(String coin,String txid) {
        try {
            stringRedisTemplate.opsForZSet().add(FormatUtils.format(RedisContant.SENT_TYPE_TRANSCATION,coin),txid,System.currentTimeMillis()/1000);
        } catch (Exception e) {
            logger.error("setTxid error",e);
        }
    }

    public void setTxSign(String txid,String sign) {
        try {
            stringRedisTemplate.opsForValue().set(FormatUtils.format(RedisContant.TRANSCATION_SIGN,txid),sign);
        } catch (Exception e) {
            logger.error("setTxid error",e);
        }
    }

    public void setNonceValue(String address,String nonce) {
        try {
            stringRedisTemplate.opsForValue().set(FormatUtils.format(RedisContant.A_N,address),nonce);
        } catch (Exception e) {
            logger.error("setTxid error",e);
        }
    }

    public String getNonce(String address) {
        try {
            return stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.A_N, address));
        } catch (Exception e) {
            logger.error("setTxid error",e);
            return "";
        }
    }


    public boolean hasAddress(String coin,String address) {
        try {
            return stringRedisTemplate.hasKey(FormatUtils.format(RedisContant.ADDRESS_TYPE,coin,address));
        } catch (Exception e) {
            return true;
        }
    }
}
