package com.zhangsl.wallet.notify.listener.coin;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import com.zhangsl.wallet.common.eth.TokenFunction;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.listener.event.BlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import rx.Subscription;

import java.math.BigInteger;

/**
 * Created by zhang.shaolong on 2018/4/23.
 */
public abstract class BaseETHTokenListner extends AbstractETHListener {

    private final static Logger logger = LoggerFactory.getLogger("listener");

    //订阅地址信息
    protected Subscription subscriptionLog;


    public BaseETHTokenListner() {
        super();
    }

    @Override
    public boolean start(BigInteger startBlock, BigInteger endBlock) {
        if (startBlock.compareTo(BigInteger.ZERO) < 0 || endBlock.compareTo(startBlock) <0) {
            return false;
        }
        try {
            //合约数据的扫描
            DefaultBlockParameterNumber startDefault = new DefaultBlockParameterNumber(startBlock);
            DefaultBlockParameterNumber endDefault = new DefaultBlockParameterNumber(endBlock);
            EthFilter filter = new EthFilter(startDefault, endDefault, getContracts());
            filter.addSingleTopic(EventEncoder.encode(TokenFunction.transferEvent()));
            Subscription subTranscationLog  =  web3j.ethLogObservable(filter).subscribe(log -> {
                contractAddress(log);
            });
        } catch (Exception e) {
            logger.error(FormatUtils.format("start {0} to {1} observable error",startBlock.toString(),endBlock.toString()));
        }
        return true;
    }

    //监听合约地址信息
    private void contractAddressObservable() {
        DefaultBlockParameterNumber startDefault = new DefaultBlockParameterNumber(startBlockNumber);
        EthFilter filter = new EthFilter(startDefault, DefaultBlockParameterName.LATEST, getContracts());
        filter.addSingleTopic(EventEncoder.encode(TokenFunction.transferEvent()));
        subscriptionLog = web3j.ethLogObservable(filter).subscribe(log -> {
            contractAddress(log);
        }, throwable -> logger.warn("contractAddressObservable observe error....", throwable));
        logger.info("contract start  Observable!"+ JSONObject.toJSONString(getContracts()));
    }

    /**
     * 需要处理的合约信息
     * @param
     */
    abstract protected String getContracts();


    private void contractAddress(Log log) {
        if(log == null) {
            return;
        }
        String address = log.getAddress();
        if (!getContracts().equalsIgnoreCase(address)) {
            return;
        }
        CoinType.CoinEnum coinEnum = EthToken.tokenMap.get(address);
        if (coinEnum == null) {
            return;
        }
        //我们指定的合约地址信息
        logger.info("log-> coin:"+coinEnum + ",address:"+log.getAddress() + ",transcation:" + log.getTransactionHash() + ",number:" + log.getBlockNumber() + ",block:" + log.getBlockHash());
        CoinType.CoinBlockEnum coinBlockEnum = CoinType.getCoinBlockEnum(coinEnum.name());
        blockService.sendBlockLogMesaage(coinBlockEnum, BlockEvent.MesageType.TRANSCATION, log, log.getBlockNumber());
    }


    @Override
    protected void startListener() {
        contractAddressObservable();
    }

    @Override
    protected void stop() {
        try {
            if (subscriptionLog != null) {
                subscriptionLog.unsubscribe();
                logger.info(FormatUtils.format("EthListener subscriptionLog close"));
            }
        } catch (Exception e) {
            logger.error(FormatUtils.format("EthListenerclose error"),e);
        }
    }
}
