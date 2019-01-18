package com.zhangsl.wallet.monitor.coin;

import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.Rediskey;
import com.zhangsl.wallet.monitor.task.AbStractSchedule;
import com.zhangsl.wallet.monitor.task.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by zhang.shaolong on 2018/4/22.
 */
@Component
public class USDTSchedule {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${usdt.time}")
    private String minuteTime;

    private Schedule schedule;

    @PostConstruct
    public void init() {
        schedule = new AbStractSchedule(stringRedisTemplate, CoinType.CoinEnum.USDT.toString(), Integer.valueOf(minuteTime)) {
            @Override
            public void callBack(String message) {
                stringRedisTemplate.opsForList().leftPush(Rediskey.ETH_WALLET_NOTIFY_KEY,message);
            }
        };
    }

    @Scheduled(cron="0 */10 * * * ? ")
    public void usdtSchedule() {
        if (schedule!= null) {
            schedule.run();
        }
    }
}
