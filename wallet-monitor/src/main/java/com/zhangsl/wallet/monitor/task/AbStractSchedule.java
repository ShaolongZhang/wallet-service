package com.zhangsl.wallet.monitor.task;

import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Created by zhang.shaolong on 2018/4/22.
 */
public abstract class AbStractSchedule implements Schedule, CallBack {

    private static final Logger logger = LoggerFactory.getLogger("monitor");

    private StringRedisTemplate stringRedisTemplate;

    private int intervalMinute; //到分的时间信息

    private String coin;

    public AbStractSchedule(StringRedisTemplate stringRedisTemplate,String coin,int intervalMinute) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.coin = coin;
        this.intervalMinute = intervalMinute;
    }

    @Override
    public void run() {
        long current = System.currentTimeMillis() / 1000;
        long score = current - intervalMinute * 60;
        Set<String> result = stringRedisTemplate.opsForZSet().rangeByScore(FormatUtils.format(RedisContant.SENT_TYPE_TRANSCATION, coin),0,score);
        if (result!=null) {
            result.forEach(txid->{
                if (!StringUtils.isEmpty(txid)) {
                    callBack(txid);
                    logger.info(FormatUtils.format("{0}->send:{1}",coin,txid));
                }
            });
        }
    }
}
