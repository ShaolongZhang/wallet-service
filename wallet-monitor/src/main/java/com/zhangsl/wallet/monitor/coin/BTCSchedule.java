package com.zhangsl.wallet.monitor.coin;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.BTCFactory;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.redis.Rediskey;
import com.zhangsl.wallet.common.util.FormatUtils;
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

import javax.annotation.PostConstruct;

/**
 * Created by zhang.shaolong on 2018/4/22.
 */
@Component
public class BTCSchedule {

    private static final Logger logger = LoggerFactory.getLogger("monitor");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${btc.time}")
    private String minuteTime;

    private Schedule schedule;

    @Autowired
    private RedisPub redisPub;

    @PostConstruct
    public void init() {
        schedule = new AbStractSchedule(stringRedisTemplate, CoinType.CoinEnum.BTC.toString(), Integer.valueOf(minuteTime)) {
            @Override
            public void callBack(String message) {
                stringRedisTemplate.opsForList().leftPush(Rediskey.BTC_WALLET_NOTIFY_KEY,message);
            }
        };
    }

    @Scheduled(cron="0 */20 * * * ? ")
    public void btcSchedule() {
        if (schedule!= null) {
            schedule.run();
        }
    }

    /**
     * 每分钟的心跳检测
     */
    @Scheduled(cron="0 */1 * * * ? ")
    public void pingSchedule() {
        try {
            JSONObject jsonObject = BTCFactory.getBitCoinService(CoinType.CoinEnum.BTC.toString()).ping();
            JsonRpcHandler.checkException(jsonObject);
            logger.info(FormatUtils.format("coin:{0},ping :ok", CoinType.CoinEnum.BTC.toString()));
        } catch (Exception e){
            //TODO 报警信息
            logger.info(FormatUtils.format("coin:{0},ping :fail", CoinType.CoinEnum.BTC.toString()));
            MonitorPub monitorPub = new MonitorPub(MonitorEnum.RPC, "BTC当前节点心跳返回false");
            redisPub.pubMessage(monitorPub);
        }
        try {
            long size = stringRedisTemplate.opsForList().size(Rediskey.BTC_NOTIFY_KEY);
            logger.info(FormatUtils.format("coin:{0},redis size :{1}", CoinType.CoinEnum.BTC.toString(),size));
            if (size > 2) {
                MonitorPub monitorPub = new MonitorPub(MonitorEnum.TRANSACTION, "BTC当前消费节点数据大于2");
                redisPub.pubMessage(monitorPub);
            }
        } catch (Exception e){
            //TODO 报警信息
            logger.info(FormatUtils.format("coin:{0},redis size error", CoinType.CoinEnum.BTC.toString()));
        }

    }


    /**
     * 监听目前的区块同步进度信息
     */
    @Scheduled(cron="0 */20 * * * ? ")
    public void btcBlockSchedule() {
        try {
            //目前接收到高度信息
            String blockNumber = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY,CoinType.CoinEnum.BTC.toString()));
            JSONObject jsonObject = BTCFactory.getBitCoinService(CoinType.CoinEnum.BTC.toString()).getBlockInfo();
            long rpcBlock = jsonObject.getJSONObject("result").getLong("blocks");
            logger.info(FormatUtils.format("coin:{0},current block:{1},rpc block:{2}", CoinType.CoinEnum.BTC.toString(),blockNumber,rpcBlock));
            //TODO 差距太大就报警
            if(rpcBlock-Long.valueOf(blockNumber)>5) {
                MonitorPub monitorPub = new MonitorPub(MonitorEnum.BLOCK,"BTC当前处理区块和节点区块差距大于10");
                redisPub.pubMessage(monitorPub);
            }
        } catch (Exception e) {
            logger.error("btcBlockSchedule error",e);
        }
    }
}
