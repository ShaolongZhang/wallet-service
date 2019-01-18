package com.zhangsl.wallet.notify.listener.coin;

import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.bean.TxType;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.redis.Rediskey;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import com.zhangsl.wallet.notify.handle.WalletNotify;
import com.zhangsl.wallet.notify.listener.event.BlockEvent;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhang.shaolong on 2018/3/16.
 */
@Component("ETH")
public class EthListener extends BaseETHListener {

    private final static Logger logger = LoggerFactory.getLogger("listener");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WalletNotify walletNotify;

    @Autowired
    private TransactionRedis transactionRedis;

    public EthListener() {
         super();
    }

    @Override
    protected void work() {
        new Thread(new EthListener.ETHListenerThread(Rediskey.ETH_WALLET_NOTIFY_KEY,web3j)).start();
    }

    @Override
    protected void transcation(Transaction tx) {
        if (tx == null) {
            return;
        }
        String from = tx.getFrom(); //转帐的人
        String to = tx.getTo(); //转帐的人
        try {
            Boolean isFrom = transactionRedis.isMyAddress(CoinType.CoinEnum.ETH.toString(),from);
            Boolean isTo = transactionRedis.isMyAddress(CoinType.CoinEnum.ETH.toString(),to);
            if (isFrom || isTo) {
                logger.info("Transaction[ "
                                + "blockNumber: " + tx.getBlockNumber() + ", "
                                + "Tx count: " + tx.getHash() + ", "
                                + "from: " + tx.getFrom() + ", "
                                + "to: " + tx.getTo() + ", "
                                + "value: " + tx.getValue() + "]"

                );
                if(!EthToken.tokenAddress.contains(to)) {
                    //合约地址的转帐，不做处理，合约自己处理
                    String address = isFrom?from:to;
                    String type = isFrom? TxType.SEND.toString():TxType.RECEIVE.toString();
                    BigDecimal amount = EthHandle.weiToEther(tx.getValue());
                    BigDecimal gasPrice = EthHandle.weiToEther(tx.getGasPrice());
                    TxBean txBean = walletNotify.buildNotify(address,CoinType.CoinEnum.ETH.toString(),amount,gasPrice,type,tx.getHash(),tx.getBlockNumber());
                    transactionRedis.addTransactoinByType(txBean);
                    ListenerNotifyBean listenerNotifyBean = new ListenerNotifyBean(type,tx.getHash());
                    blockService.sendBlockMesaage(CoinType.CoinBlockEnum.ETH, BlockEvent.MesageType.TRANSCATION, listenerNotifyBean, tx.getBlockNumber());

                    if(type.equals(TxType.SEND.toString()) && isTo) {
                        //TO 也是我们交易所的用户信息
                        TxBean txBeanTo = walletNotify.buildNotify(to,CoinType.CoinEnum.ETH.toString(),amount,gasPrice,TxType.RECEIVE.toString(),tx.getHash(),tx.getBlockNumber());
                        transactionRedis.addTransactoinByType(txBeanTo);
                        listenerNotifyBean = new ListenerNotifyBean(TxType.RECEIVE.toString(),tx.getHash());
                        blockService.sendBlockMesaage(CoinType.CoinBlockEnum.ETH, BlockEvent.MesageType.TRANSCATION, listenerNotifyBean, tx.getBlockNumber());
                    }
                    //发送通知记录信息
                    if(isFrom) {
                        transactionRedis.deleteTxid(CoinType.CoinEnum.ETH.toString(),tx.getHash());
                    }
                }

            }
        } catch (Exception e) {
            logger.error("ethGetTransactionReceipt error",e);
        }
    }


    @Override
    protected void contractAddress(Log log) {
        if(log == null) {
            return;
        }
        String address = log.getAddress();
        CoinType.CoinEnum coinEnum = EthToken.tokenMap.get(address);
        if (coinEnum == null) {
            return;
        }
        //我们指定的合约地址信息
//        logger.info("log-> coin:"+coinEnum + ",address:"+log.getAddress() + ",transcation:" + log.getTransactionHash() + ",number:" + log.getBlockNumber() + ",block:" + log.getBlockHash());
        CoinType.CoinBlockEnum coinBlockEnum = CoinType.getCoinBlockEnum(coinEnum.name());
        blockService.sendBlockLogMesaage(coinBlockEnum, BlockEvent.MesageType.TRANSCATION, log, log.getBlockNumber());
    }

    @Override
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
        transactionRedis.setValue(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY,CoinType.CoinBlockEnum.ETH.toString()),ethBlock.getNumber().toString());
        blockEther(ethBlock);
    }

    private void blockEther(EthBlock.Block ethBlock) {
        //TODO 有多少个代币，就通知多少个监听的代币
        blockService.sendBlockMesaage(CoinType.CoinBlockEnum.ETH,BlockEvent.MesageType.BLOCK, null, ethBlock.getNumber());
        blockService.sendBlockMesaage(CoinType.CoinBlockEnum.EOS,BlockEvent.MesageType.BLOCK, null, ethBlock.getNumber());
        blockService.sendBlockMesaage(CoinType.CoinBlockEnum.TB,BlockEvent.MesageType.BLOCK, null, ethBlock.getNumber());
        blockService.sendBlockMesaage(CoinType.CoinBlockEnum.USDT,BlockEvent.MesageType.BLOCK, null, ethBlock.getNumber());

    }


    private class ETHListenerThread implements  Runnable {
        private String key;
        private Web3j web3j;

        public ETHListenerThread(String key,Web3j web3j) {
            this.key = key;
            this.web3j = web3j;
        }

        @Override
        public void run() {
            while(true){
                String result = stringRedisTemplate.opsForList().leftPop(key, 5, TimeUnit.SECONDS);
                try {
                    if (!StringUtils.isEmpty(result)) {
                        Transaction transaction = EthHandle.getInstance().getTranscation(this.web3j,result).get();
                        transcation(transaction);
                        TransactionReceipt transactionReceipt = EthHandle.getInstance().getReceipt(this.web3j,result).get();
                        List<Log> logs =  transactionReceipt.getLogs();
                        if(logs!=null) {
                            logs.forEach(log->{
                                contractAddress(log);
                            });
                        }
                    }
                } catch (Exception e) {
                    logger.error(FormatUtils.format("{0} blcock info {1} error", CoinType.CoinEnum.ETH.toString(),result),e);
                }
            }
        }
    }
}
