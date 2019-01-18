package com.zhangsl.wallet.monitor.coin;

import com.zhangsl.wallet.blockcoin.common.BlockProperties;
import com.zhangsl.wallet.blockcoin.eth.EthCoinService;
import com.zhangsl.wallet.blockcoin.rpc.RpcService;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.Rediskey;
import com.zhangsl.wallet.monitor.common.EthTxCommon;
import com.zhangsl.wallet.monitor.task.AbStractSchedule;
import com.zhangsl.wallet.monitor.task.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Created by zhang.shaolong on 2018/4/22.
 */
@Component
public class EOSSchedule {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${eos.time}")
    private String minuteTime;

    private Schedule schedule;

    private EthCoinService ethCoinService;

    @PostConstruct
    public void init() {
        ethCoinService = EthCoinService.buildService(RpcService.buildETHService(new RestTemplate(BlockProperties.create())));

        schedule = new AbStractSchedule(stringRedisTemplate, CoinType.CoinEnum.EOS.toString(), Integer.valueOf(minuteTime)) {
            @Override
            public void callBack(String message) {
                if(EthTxCommon.validateSend(ethCoinService, stringRedisTemplate,message)) {
                    stringRedisTemplate.opsForList().leftPush(Rediskey.ETH_WALLET_NOTIFY_KEY,message);
                }
            }
        };
    }

    @Scheduled(cron="0 */5 * * * ? ")
    public void eosSchedule() {
        if (schedule!= null) {
            schedule.run();
        }
    }
}
