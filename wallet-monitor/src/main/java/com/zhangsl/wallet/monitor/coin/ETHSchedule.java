package com.zhangsl.wallet.monitor.coin;

import com.zhangsl.wallet.blockcoin.eth.EthCoinService;
import com.zhangsl.wallet.blockcoin.rpc.RpcService;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.redis.Rediskey;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.monitor.common.EthTxCommon;
import com.zhangsl.wallet.monitor.redis.MonitorEnum;
import com.zhangsl.wallet.monitor.redis.MonitorPub;
import com.zhangsl.wallet.monitor.redis.RedisPub;
import com.zhangsl.wallet.monitor.task.AbStractSchedule;
import com.zhangsl.wallet.monitor.task.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

/**
 * Created by zhang.shaolong on 2018/4/22.
 */
@Component
public class  ETHSchedule {

    private static final Logger logger = LoggerFactory.getLogger("monitor");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${eth.time}")
    private String minuteTime;

    private Schedule schedule;

    private EthCoinService ethCoinService;

    @Value("${eth.listener.url}")
    private String ethListenerUrl;

    @Autowired
    private RedisPub redisPub;

    @PostConstruct
    public void init() {
        ethCoinService = EthCoinService.buildService(RpcService.buildService(ethListenerUrl,null,new RestTemplate()));
        schedule = new AbStractSchedule(stringRedisTemplate, CoinType.CoinEnum.ETH.toString(), Integer.valueOf(minuteTime)) {
            @Override
            public void callBack(String message) {
                if(EthTxCommon.validateSend(ethCoinService, stringRedisTemplate,message)) {
                    stringRedisTemplate.opsForList().leftPush(Rediskey.ETH_WALLET_NOTIFY_KEY,message);
                }
            }
        };
    }

    /**
     * 心跳检测
     */
    @Scheduled(cron="0 */1 * * * ? ")
    public void ethListener() {
        try {
            boolean result = ethCoinService.netListening();
            logger.info(FormatUtils.format("coin:{0},ping :{1}", CoinType.CoinEnum.ETH.toString(), result));
            if (!result) {
                MonitorPub monitorPub = new MonitorPub(MonitorEnum.RPC, "ETH当前节点心跳返回false");
                redisPub.pubMessage(monitorPub);
            }
        } catch (Exception e){
            //TODO 报警信息
            logger.info(FormatUtils.format("coin:{0},ping :false", CoinType.CoinEnum.ETH.toString()));
            MonitorPub monitorPub = new MonitorPub(MonitorEnum.RPC, "ETH当前节点心跳返回false");
            redisPub.pubMessage(monitorPub);
        }
    }


    /**
     * 发送数据检测，防止没有收到，重新请求
     */
    @Scheduled(cron="0 */5 * * * ? ")
    public void ethSchedule() {
        if (schedule!= null) {
            schedule.run();
        }
    }
    /**
     * 监听目前的区块同步进度信息
     */
    @Scheduled(cron="0 */5 * * * ? ")
    public void ethBlockSchedule() {
        try {
            //目前接收到高度信息
            String blockNumber = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY,CoinType.CoinEnum.ETH.toString()));
            BigInteger blockNumer  = ethCoinService.getBlockNumber();
            logger.info(FormatUtils.format("coin:{0} current block:{1},rpc block:{2}",CoinType.CoinEnum.ETH.toString(),blockNumber,blockNumer.longValue()));
            //TODO 差距太大就报警
            if(blockNumer.longValue()-Long.valueOf(blockNumber)>100) {
                MonitorPub monitorPub = new MonitorPub(MonitorEnum.BLOCK,"ETH当前处理区块和节点区块差距大于100");
                redisPub.pubMessage(monitorPub);
            }
        } catch (Exception e) {
            logger.error("ethBlockSchedule error",e);
        }
    }

}
