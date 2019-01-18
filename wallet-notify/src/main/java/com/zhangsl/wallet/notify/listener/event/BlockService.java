package com.zhangsl.wallet.notify.listener.event;


import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.concurrent.*;

/**
 *
 * 区块和交易的信息直接给这个发消息就行了,处理消息信息
 *
 * Created by zhang.shaolong on 2018/3/10.
 */
@Service
public class BlockService {

    /**  发送消息信息 **/
    private BlockEventMulticaster blockEventMulticaster;

    @Autowired
    private BlockListener blockListener;

    private static final int COREPOOL_SIZE = 50;

    private static final int MAX_IMUMPOOL_SIZE = 100;

    private static final int CAPACITY = 1024;

    private static BlockingQueue<Runnable> queue = new ArrayBlockingQueue(CAPACITY);


    @PostConstruct
    private void init () {
        //设置线程池处理
        ExecutorService executorService = new ThreadPoolExecutor(COREPOOL_SIZE,MAX_IMUMPOOL_SIZE, 200, TimeUnit.MILLISECONDS,queue);

        blockEventMulticaster  = new BlockEventMulticaster();
        //设置20个线程处理
        blockEventMulticaster.setTaskExecutor(executorService);

        blockEventMulticaster.addApplicationListener(blockListener);
    }


    //通用的发送block信息
    public void sendBlockMesaage (CoinType.CoinBlockEnum coinBLockEnum, BlockEvent.MesageType mesageType, ListenerNotifyBean message) {
        BlockEvent blockEvent =  new BlockEvent(this,coinBLockEnum,mesageType,message);
        blockEventMulticaster.multicastEvent(blockEvent);
    }

    public void sendBlockMesaage (CoinType.CoinBlockEnum coinBLockEnum, BlockEvent.MesageType mesageType, ListenerNotifyBean notifyBean, BigInteger numberBlock) {
        BlockEvent blockEvent =  new BlockEvent(this,coinBLockEnum,mesageType,notifyBean,numberBlock);
        blockEventMulticaster.multicastEvent(blockEvent);
    }


    //以太坊系列的的用的web3j
    public void sendBlockLogMesaage (CoinType.CoinBlockEnum coinBLockEnum,BlockEvent.MesageType mesageType, Log log, BigInteger numberBlock) {
        BlockEvent blockEvent =  new BlockEvent(this,coinBLockEnum, mesageType,log,numberBlock);
        blockEventMulticaster.sendEvent(blockEvent);
    }


}
