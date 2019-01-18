package com.zhangsl.wallet.notify.listener.coin;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.EthWeb3jClient;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.eth.TokenFunction;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Subscription;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhang.shaolong on 2018/4/13.
 */
public abstract class BaseETHListener  extends BaseListener {

    private final static Logger logger = LoggerFactory.getLogger("listener");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //太坊客户端
    protected Web3j web3j;

    //订阅地址信息
    protected Subscription subscription;

    //订阅地址信息
    protected Subscription subscriptionLog;

    //订阅地址信息
    protected Subscription subscriptionTx;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final AtomicBoolean isStart ;

    private long startBlockNumber;

    @Value("${eth.listener.url}")
    private String ethListenerUrl;


    public BaseETHListener() {
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

                web3j = EthWeb3jClient.getInstance().getClient(ethListenerUrl,TimeUnit.SECONDS.toMillis(30));
                //获取上一次处理的区块信息
                //区块全部监听
                String number = stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY, CoinType.CoinBlockEnum.ETH.toString()));
                if(!StringUtils.isEmpty(number)) {
                    startBlockNumber = Long.valueOf(number);
                }
                //区块的最新高度信息
                BigInteger ethBlockNumber = EthHandle.getInstance().getBlock(web3j);
                if (startBlockNumber<=0 || startBlockNumber>ethBlockNumber.longValue()) {
                    startBlockNumber = ethBlockNumber.longValue()-1;
                }
                //启动redis 记录的区块和ethNumber区块的遍历
                logger.info(FormatUtils.format("ethListener start redis block:{0} rpc block:{1}",startBlockNumber, ethBlockNumber.intValue()));
                contractAddressObservable();
                //添加合约地址信息
                txObservable();
                //最后在监听区块
                blockObservable();

                //启动一个线程去处理未处理的区块信息
                if (startBlockNumber < ethBlockNumber.longValue()) {
                    //需要处理数据信息
                    BigInteger startBlock = BigInteger.valueOf(startBlockNumber-1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            start(startBlock, ethBlockNumber);
                        }
                    }).start();
                    logger.info(FormatUtils.format("ethListener start thread scan transaction startblock:{0} ,endblock:{1}", startBlockNumber, ethBlockNumber.intValue()));

                }
                addShutdownHook();
                work();

                //启动监听
                //new HeartListener(this.subscription);
                //new HeartListener(this.subscriptionTx);
                //new HeartListener(this.subscriptionLog);

            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean start(BigInteger startBlock, BigInteger endBlock) {
        if (startBlock.compareTo(BigInteger.ZERO) < 0 || endBlock.compareTo(startBlock) <0) {
            return false;
        }
        try {
            //交易数据的扫描
            Subscription subTranscation = web3j.
                    replayTransactionsObservable(
                            DefaultBlockParameter.valueOf(startBlock),
                            DefaultBlockParameter.valueOf(endBlock)).
                    subscribe(transaction -> {
                        transcation(transaction);});

            //合约数据的扫描
            DefaultBlockParameterNumber startDefault = new DefaultBlockParameterNumber(startBlock);
            DefaultBlockParameterNumber endDefault = new DefaultBlockParameterNumber(endBlock);
            EthFilter filter = new EthFilter(startDefault, endDefault, EthToken.tokenAddress);
            filter.addSingleTopic(EventEncoder.encode(TokenFunction.transferEvent()));
            Subscription subTranscationLog  =  web3j.ethLogObservable(filter).subscribe(log -> {
                contractAddress(log);
            });
        } catch (Exception e) {
            logger.error(FormatUtils.format("start {0} to {1} observable error",startBlock.toString(),endBlock.toString()));
        }
        return true;
    }


    private void blockObservable() {
        //区块全部监听
        subscription = EthWeb3jClient.getInstance().getClient(ethListenerUrl,TimeUnit.SECONDS.toMillis(20)).blockObservable(false).subscribe(block -> {
            block(block);
        }, throwable -> logger.warn("block observe error....",throwable));
        logger.info("blockObservable start  Observable!");

    }

    //交易监听
    private void txObservable() {
        subscriptionTx = web3j.transactionObservable().subscribe(tx -> {
            transcation(tx);
        }, Throwable::printStackTrace, () -> logger.warn("transaction observe done...."));
        logger.info("transaction start  Observable!");
    }

    //监听合约地址信息
    private void contractAddressObservable() {
        DefaultBlockParameterNumber startDefault = new DefaultBlockParameterNumber(startBlockNumber);
        EthFilter filter = new EthFilter(startDefault, DefaultBlockParameterName.LATEST, EthToken.tokenAddress);
        filter.addSingleTopic(EventEncoder.encode(TokenFunction.transferEvent()));
        subscriptionLog = web3j.ethLogObservable(filter).subscribe(log -> {
            contractAddress(log);
        }, throwable -> logger.warn("contractAddressObservable observe error....", throwable));
        logger.info("contract start  Observable!"+ JSONObject.toJSONString(EthToken.tokenAddress));
    }


    /**
     * 自己的工作
     */
    abstract protected void work();

//    /**
//     * 需要处理的合约信息
//     * @param
//     */
//    abstract protected List<String> getContracts();

    /**
     * 区块的监听处理
     * @param block
     */
    abstract protected void block(EthBlock block);

    /**
     * 交易的监听处理
     * @param tx
     */
    abstract protected void transcation(Transaction tx);

    /**
     * 合约的监听处理
     *
     * @param log
     */
    abstract protected void contractAddress(Log log);


    private void addShutdownHook () {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    close();
                    logger.info("EthListener! close client succ!");
                } catch (Exception e) {
                    logger.error( "EthListener! close client fail",e);
                }
            }
        }));
    }


    /**
     * 关闭订阅
     */
    private  void close() {
        try {
            if (subscription != null) {
                subscription.unsubscribe();
                logger.info(FormatUtils.format("EthListener subscription close"));
            }

            if (subscriptionTx != null) {
                subscriptionTx.unsubscribe();
                logger.info(FormatUtils.format("EthListener subscriptionTx close"));
            }
            if (subscriptionLog != null) {
                subscriptionLog.unsubscribe();
                logger.info(FormatUtils.format("EthListener subscriptionLog close"));
            }
            isStart.set(false);
        } catch (Exception e) {
            logger.error(FormatUtils.format("EthListenerclose error"),e);
        }
    }
}
