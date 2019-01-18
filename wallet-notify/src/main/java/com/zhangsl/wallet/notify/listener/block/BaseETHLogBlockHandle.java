package com.zhangsl.wallet.notify.listener.block;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.EthWeb3jClient;
import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.bean.TxType;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.coin.EthToken;
import com.zhangsl.wallet.common.eth.EthConstant;
import com.zhangsl.wallet.common.eth.EthTokenHandle;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import com.zhangsl.wallet.notify.handle.WalletNotify;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/4/9.
 */
public abstract class BaseETHLogBlockHandle extends BaseETHBlockHandle<Log>{

    private static final Logger logger = LoggerFactory.getLogger("listener");

    @Autowired
    private WalletNotify walletNotify;

    @Autowired
    private TransactionRedis transactionRedis;

    private Map<String,Integer> decimalsMap = new HashMap<String,Integer>();

    @Override
    public void handle(Log log, Map<String,Object> params) {
        if( null == log) {
            return;
        }
        String address = log.getAddress();
        //我们指定的合约地址信息
        CoinType.CoinEnum coinEnum = EthToken.tokenMap.get(address);
        if (coinEnum == null) {
            return;
        }
        EventValues eventValues = EthTokenHandle.getInstance().extractEventLog(log);
        if (eventValues == null) {
            logger.warn(FormatUtils.format("coin:{0},blocknumber:{1},hash:{2} event is null", coinEnum.name(), log.getBlockNumber(),log.getTransactionHash()));
            return;
        }
        //判断下账号是否是我们的账号信息
        Address from = (Address) eventValues.getIndexedValues().get(0); //转的币
        Address to = (Address) eventValues.getIndexedValues().get(1);
        Boolean isFrom = transactionRedis.isMyAddress(coinEnum.toString(), from.toString());
        Boolean isTo = transactionRedis.isMyAddress(coinEnum.toString(), to.toString());
        //是我们交易所的用户信息
        if (isFrom || isTo) {
            Uint256 value = (Uint256) eventValues.getNonIndexedValues().get(0);
            int decimals = getDecimals(address);
            BigDecimal amout = EthConstant.getTokenAmount(value.getValue().toString(),decimals);
            String ads = isFrom ? from.toString() : to.toString();
            String type = isFrom ? TxType.SEND.toString() : TxType.RECEIVE.toString();
            logger.info(FormatUtils.format("coin:{0},from:{1},to:{2},value:{3},type:{4}", coinEnum.name(), from, to, amout.doubleValue(),type));
            TxBean txBean = walletNotify.buildNotify(ads, coinEnum.toString(), amout, null, type, log.getTransactionHash(), log.getBlockNumber());
            transactionRedis.addTransactoinByType(txBean);
            ListenerNotifyBean listenerNotifyBean = new ListenerNotifyBean(type, log.getTransactionHash());
            transactionRedis.setValueByScore(FormatUtils.format(RedisContant.TRANSCATION_TYPE, coinEnum.toString()), JSONObject.toJSONString(listenerNotifyBean), log.getBlockNumber().doubleValue());
            if (type.equals(TxType.SEND.toString()) && isTo) {
                //TO 也是我们交易所的用户信息
                logger.info(FormatUtils.format("coin:{0},from:{1},to:{2},value:{3},type:{4}", coinEnum.name(), from, to, amout,TxType.RECEIVE.toString()));
                TxBean txBeanTo = walletNotify.buildNotify(to.toString(), coinEnum.toString(), amout, null, TxType.RECEIVE.toString(), log.getTransactionHash(), log.getBlockNumber());
                transactionRedis.addTransactoinByType(txBeanTo);
                ListenerNotifyBean receiveNotifyBean = new ListenerNotifyBean(TxType.RECEIVE.toString(), log.getTransactionHash());
                transactionRedis.setValueByScore(FormatUtils.format(RedisContant.TRANSCATION_TYPE, coinEnum.toString()), JSONObject.toJSONString(receiveNotifyBean), log.getBlockNumber().doubleValue());
            }
            //发送通知记录信息
            if(isFrom) {
                transactionRedis.deleteTxid(coinEnum.toString(),log.getTransactionHash());
            }
        }

    }

    public int getDecimals(String address) {
        Integer decimals = decimalsMap.get(address);
        if (decimals == null) {
            int deci = EthTokenHandle.getInstance().getTokenDecimals(EthWeb3jClient.getInstance().getClient(), address);
            decimals = Integer.valueOf(deci);
            decimalsMap.put(address,decimals);
        }
        return decimals.intValue();
    }
}
