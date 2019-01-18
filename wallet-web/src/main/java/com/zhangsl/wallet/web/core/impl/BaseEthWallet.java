package com.zhangsl.wallet.web.core.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.EthWeb3jClient;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.blockcoin.eth.EthCoinService;
import com.zhangsl.wallet.blockcoin.rpc.JSONRPCService;
import com.zhangsl.wallet.blockcoin.rpc.RpcService;
import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.eth.EthConstant;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.eth.EthTokenHandle;
import com.zhangsl.wallet.common.exception.WalletException;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.web.core.Wallet;
import com.zhangsl.wallet.web.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/4/3.
 */
public abstract  class BaseEthWallet implements Wallet {

    private static final Logger logger = LoggerFactory.getLogger("controller");

    protected  Web3j web3j;

    protected EthCoinService ethCoinService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void initWallet() {
        web3j = EthWeb3jClient.getInstance().getClient();
        JSONRPCService jsonrpcService = RpcService.buildETHService(restTemplate);
        ethCoinService = EthCoinService.buildService(jsonrpcService);
    }


    @Override
    public String sendSignedTransaction(String coin,String transactionString) {
        JSONObject jsonObject = ethCoinService.sendRawTransaction(transactionString);
        logger.info(FormatUtils.format("walletTransfer sendSignedTransaction,hash-->{0},result:{1}", transactionString, jsonObject));
        JsonRpcHandler.checkException(jsonObject);
        return jsonObject.getString("result");
    }


    protected String rawTransaction(String fromAddress, String toAddress, String contractAddress, BigInteger amount, BigInteger gasPrice,BigInteger balance) {

        RawTransaction rawTransaction = null;
        if (StringUtils.isEmpty(contractAddress)) {
            rawTransaction =  EthHandle.getInstance().createRawTransaction(web3j,fromAddress, toAddress, amount, gasPrice, EthConstant.GAS_LIMIT);
        } else {
            rawTransaction = EthTokenHandle.getInstance().createTokenRawTransaction(web3j, fromAddress, toAddress, contractAddress, amount, gasPrice, EthConstant.TOKEN_GAS_LIMIT);
        }
        //nonce 判断
        BigInteger nonce = rawTransaction.getNonce();
        //判断当前的nonce和现在是否一致
//        BigInteger currentNew = EthHandle.getInstance().getNonce(web3j,fromAddress);
//        if (nonce.compareTo(currentNew) ==1) {
//            //说明有等待发送的nonce直接报错
//            nonce = currentNew;//
////            throw new ThinkbitWalletException(ErrorCode.TRANSCATION_ERROR);
//        }
//        String redisNonce = redisService.getNonce(fromAddress);
//        if (!StringUtils.isEmpty(redisNonce)) {
//            BigInteger redis = BigInteger.valueOf(Long.valueOf(redisNonce));
//            //nonce 比上一次用的小
//            if (nonce.compareTo(redis) < 1) {
//                throw new ThinkbitWalletException(ErrorCode.TRANSCATION_ERROR);
//            }
//        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from",fromAddress);
        jsonObject.put("to",rawTransaction.getTo());
        jsonObject.put("value",Numeric.encodeQuantity(rawTransaction.getValue()));
        jsonObject.put("data",rawTransaction.getData());
        BigInteger gas = validateGas(fromAddress,rawTransaction,rawTransaction.getGasLimit(),balance);
        jsonObject.put("gas", Numeric.encodeQuantity(gas));
        jsonObject.put("nonce",Numeric.encodeQuantity(BigInteger.ZERO));
        jsonObject.put("gasPrice",Numeric.encodeQuantity(rawTransaction.getGasPrice()));
        redisService.setNonceValue(fromAddress,nonce.toString());
        return jsonObject.toString();

    }

    public BigInteger validateGas(String from,RawTransaction rawTransaction,BigInteger limit,BigInteger balance) {
        //预计要用到的gas
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("from",from);
        if(!StringUtils.isEmpty(rawTransaction.getTo())) {
            map.put("to",rawTransaction.getTo());
        }
        if(rawTransaction.getValue().compareTo(BigInteger.ZERO) ==1) {
            map.put("value",Numeric.encodeQuantity(rawTransaction.getValue()));
        }
        if (!StringUtils.isEmpty(rawTransaction.getData())) {
            if (rawTransaction.getData().startsWith("0x")) {
                map.put("data",rawTransaction.getData());
            } else {
                map.put("data","0x"+rawTransaction.getData());
            }
        }
        //预计估计用到的gas
        BigInteger bigInteger  = ethCoinService.estimateGas(map);
        //预计的大概手续费是
        BigInteger gasUse = bigInteger.multiply(rawTransaction.getGasPrice());
        if(rawTransaction.getValue().add(gasUse).compareTo(balance) ==1) {
            throw new WalletException(ErrorCode.ETH_ENOUGH_ERROR);
        }
        //TODO 判断gasusd
        if (bigInteger.compareTo(limit)==1) {
            //在原来的基础上加100个
            return bigInteger.add(BigInteger.valueOf(100L));
        }
        return rawTransaction.getGasLimit();
    }

}
