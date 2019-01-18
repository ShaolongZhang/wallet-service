package com.zhangsl.wallet.monitor.common;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.blockcoin.eth.EthCoinService;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.common.util.OkHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * Created by zhang.shaolong on 2018/5/16.
 */
public class EthTxCommon {

    private static final Logger logger = LoggerFactory.getLogger(EthTxCommon.class);


    private final static String ETHER_API = "https://api.etherscan.io/api";

    private final static String METHOD_HASH = "eth_getTransactionByHash";

    private final static String METHOD_NUMBER = "eth_blockNumber";


    //根据这个可以查询余额信息
    public static final String ETHER_API_BASE = ETHER_API +"?module={0}&action={1}&apikey={2}";

    private final static String key = "4T8IJCMITUFQPSS2DKY41W7X2QMI865CRW";


    public static  boolean validateSend(EthCoinService ethCoinService, StringRedisTemplate stringRedisTemplate, String txHash) {
        boolean isInblock = isInBlock(txHash);
        if(!isInblock) {
            isInblock = isInBlockByRpc(ethCoinService,txHash);
        }
        if(!isInblock) {
            if(remove(ethCoinService,txHash)){
               String signTx =  stringRedisTemplate.opsForValue().get(FormatUtils.format(RedisContant.TRANSCATION_SIGN,txHash));
               if (!StringUtils.isEmpty(signTx)) {
                   JSONObject jsonObject =  ethCoinService.sendRawTransaction(signTx);
                   return validateTx(jsonObject);
               }
            }
            return false;
        }
        return true;
    }

    public static boolean isInBlock(String txHash) {
        if (StringUtils.isEmpty(txHash)) {
            return true;
        }
        String url = FormatUtils.format(ETHER_API_BASE, "proxy", METHOD_HASH, key)+"&txhash="+txHash;
        String result = OkHttpUtils.get(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return validateTx(jsonObject);
    }

    public static boolean isInBlockByRpc(EthCoinService ethCoinService,String txHash) {
        if (StringUtils.isEmpty(txHash)) {
            return true;
        }
        JSONObject jsonObject = ethCoinService.getTransaction(txHash);
        boolean result = validateTx(jsonObject);
        if (result == false){
            return result;
        }
        return jsonObject.getJSONObject("result") == null ? false :true;

    }

    public static boolean remove(EthCoinService ethCoinService,String txHash) {
        if (StringUtils.isEmpty(txHash)) {
            return true;
        }
        JSONObject jsonObject =  ethCoinService.removeTx(txHash);
        boolean result = validateTx(jsonObject);
        if (result == false){
            return result;
        }
        return jsonObject.getJSONObject("result") == null ? false :true;

    }

    private static boolean validateTx(JSONObject jsonObject) {
        try {
            JsonRpcHandler.checkException(jsonObject);
            return true;
        } catch (Exception e) {
            logger.error("validateTx error",e);
            return false;
        }
    }
}
