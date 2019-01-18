package com.zhangsl.wallet.notify.listener.coin;

import com.zhangsl.wallet.blockcoin.common.EthWeb3jClient;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.listener.event.BlockEvent;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import rx.Subscription;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhang.shaolong on 2018/4/23.
 */
public abstract class AbstractETHListener extends BaseListener{

    private final static Logger logger = LoggerFactory.getLogger("listener");

    //太坊客户端
    protected Web3j web3j;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final AtomicBoolean isStart ;

    protected long startBlockNumber;

    protected BigInteger ethBlockNumber;

    //订阅地址信息
    protected Subscription subscription;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TransactionRedis transactionRedis;

    public AbstractETHListener() {
        this.isStart = new AtomicBoolean(false);
    }

    /**
     * 监控区块信息
     */
    @Override
    public boolean start() {
        final ReentrantLock lock = this.reentrantLock;
        lock.lock();
        try {
            if (isStart.compareAndSet(false, true)) {
                web3j = EthWeb3jClient.getInstance().getClient();
                //获取上一次处理的区块信息
                //区块全部监听
                String number = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY, getBlock().toString()));
                if(!StringUtils.isEmpty(number)) {
                    startBlockNumber = Long.valueOf(number);
                }
                //区块的最新高度信息
                ethBlockNumber = EthHandle.getInstance().getBlock(web3j);
                if (startBlockNumber<=0 || startBlockNumber>ethBlockNumber.longValue()) {
                    startBlockNumber = ethBlockNumber.longValue()-1;
                }
                //启动一个线程去处理未处理的区块信息
                if (startBlockNumber < ethBlockNumber.longValue()) {
                    //需要处理数据信息
                    BigInteger startBlock = BigInteger.valueOf(startBlockNumber);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            start(startBlock, ethBlockNumber);
                        }
                    }).start();
                    logger.info(FormatUtils.format("ethListener start thread scan transaction startblock:{0} ,endblock:{1}", startBlockNumber, ethBlockNumber.intValue()));

                }
                //启动redis 记录的区块和ethNumber区块的遍历
                logger.info(FormatUtils.format("ethListener start redis block:{0} rpc block:{1}",startBlockNumber, ethBlockNumber.intValue()));
                startListener();
                //最后在监听区块
                blockObservable();
                addShutdownHook();
            }
        } finally {
            lock.unlock();
        }
        return true;
    }


    private void blockObservable() {
        //区块全部监听
        subscription = web3j.blockObservable(false).subscribe(block -> {
            block(block);
        }, throwable -> logger.warn("block observe error....",throwable));
        logger.info("blockObservable start  Observable!");
    }

    protected void block(EthBlock block) {
        if (block == null) {
            return;
        }
        EthBlock.Block ethBlock = block.getBlock();
        int transactionCount = ethBlock.getTransactions().size();
        String hash = ethBlock.getHash();
        String parentHash = ethBlock.getParentHash();
        logger.info("block["
                + "blockNumber: " + ethBlock.getNumber() + ", "
                + "Tx count: " + transactionCount + ", "
                + "Hash: " + hash + ", "
                + "Parent hash: " + parentHash+ "]"
        );
        //需要确认高度信息，高度信息确认就是为了有效确定
        transactionRedis.setValue(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY,getBlock().toString()),ethBlock.getNumber().toString());
        blockEther(ethBlock);
    }

    private void blockEther(EthBlock.Block ethBlock) {
        blockService.sendBlockMesaage(getBlock(), BlockEvent.MesageType.BLOCK, null, ethBlock.getNumber());
    }

    protected abstract CoinType.CoinBlockEnum getBlock();


    private void addShutdownHook () {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stop();
                    if (subscription!=null) {
                        subscription.unsubscribe();
                    }
                    isStart.set(false);
                    logger.info("EthListener! close client succ!");
                } catch (Exception e) {
                    logger.error( "EthListener! close client fail",e);
                }
            }
        }));
    }

    protected abstract void startListener();;

    protected abstract void stop();
}
