package com.zhangsl.wallet.monitor.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by zhang.shaolong on 2018/5/27.
 */
@Component
public class RedisPub {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${redis.pub.warn}")
    private String pubTopic;

    public void pubMessage(MonitorPub monitor) {
        if (null == monitor) {
            return;
        }
        String message = JSONObject.toJSONString(monitor);
        if(!StringUtils.isEmpty(message)) {
            stringRedisTemplate.convertAndSend(pubTopic,message);
        }
    }
}
