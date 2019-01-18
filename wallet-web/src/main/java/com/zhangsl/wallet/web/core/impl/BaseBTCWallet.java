package com.zhangsl.wallet.web.core.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.BTCFactory;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.web.bean.TransactionMessage;
import com.zhangsl.wallet.web.core.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;

/**
 * Created by zhang.shaolong on 2018/4/23.
 */
public abstract  class BaseBTCWallet implements Wallet {

    private static final Logger logger = LoggerFactory.getLogger("controller");

    private final static String RESULT = "result";

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    /**
     * 通用的查询接口
     * @param coin
     * @param transactionId
     * @return
     */
    @Override
    public TransactionMessage getTransaction(String coin, String transactionId) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(coin).getTransaction(transactionId);
        JsonRpcHandler.checkException(jsonObject);
        return null;
    }


    /**
     * 通用的发送接口
     *
     * @param coin
     * @param transactionString
     * @return
     */
    @Override
    public String sendSignedTransaction(String coin,String transactionString) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(coin).sendRawTransaction(transactionString);
        JsonRpcHandler.checkException(jsonObject);
        String result =  jsonObject.getString(RESULT);
        logger.info(FormatUtils.format("{0} sendSignedTransaction result: {1} ",coin,result));
        return result;
    }

    /**
     * 通用的查询余额接口
     *
     * @param coin
     * @param address
     * @return
     */
    @Override
    public BigDecimal queryBalance(String coin,String address) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(coin).getBalance(address,6);
        JsonRpcHandler.checkException(jsonObject);
        return jsonObject.getBigDecimal(RESULT);
    }
}
