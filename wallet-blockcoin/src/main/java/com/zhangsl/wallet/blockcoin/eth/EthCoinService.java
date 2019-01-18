package com.zhangsl.wallet.blockcoin.eth;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.blockcoin.rpc.JSONRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/3/23.
 */
public class EthCoinService {

    private static final Logger logger = LoggerFactory.getLogger(EthCoinService.class);

    private final static String RESULT = "result";

    private JSONRPCService ethService;

    private EthCoinService(JSONRPCService jsonrpcService) {
        this.ethService = jsonrpcService;
    }

    public static EthCoinService buildService(JSONRPCService jsonrpcService) {
        return new EthCoinService(jsonrpcService);
    }


    public boolean netListening() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.NET_LISTENING.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("netListening error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return jsonObject.getBooleanValue(RESULT);
    }

    public BigInteger getGasPrice() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_GASPRICE.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getGasPrice error",throwable);
        }
        return Numeric.decodeQuantity(jsonObject.getString(RESULT));
    }

    public BigInteger getBalance(String address) {
        Object[]  params = new Object[2];
        params[0] = address;
        params[1] = "latest";
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_BALANCE.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getBalance error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return Numeric.decodeQuantity(jsonObject.getString(RESULT));
    }

    public BigInteger getBlockNumber() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_BLOCKNUMBER.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getBlockNumber error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return Numeric.decodeQuantity(jsonObject.getString(RESULT));
    }


    public JSONObject getTransctionCount(String address) {
        Object[]  params = new Object[2];
        params[0] = address;
        params[1] = "latest";
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_GET_TRANSACTIONCOUNT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        return jsonObject;
    }


    public JSONObject sendRawTransaction(String data) {
        Object[]  params = new Object[1];
        params[0] = data;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_SENDRAW_TRNSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return jsonObject;
    }

    public JSONObject sign(String address,String data) {
        Object[]  params = new Object[2];
        params[0] = address;
        params[1] = data;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_SIGN.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        return jsonObject;
    }

    public JSONObject signTransaction(String data) {
        Object[]  params = new Object[1];
        params[0] = data; //json串 所有的必须十六进制，而且账户必须unlock
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_SIGNTRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return jsonObject;
    }


    public JSONObject sha3(String data) {
        Object[]  params = new Object[1];
        params[0] = data;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.WEB_SHA3.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        return jsonObject;
    }

    public BigInteger estimateGas(Map<String,Object> data) {
        Object[] params = new Object[1];
        params[0] = data;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_ESTIMATE_GAS.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getGasPrice error",throwable);
        }
        JsonRpcHandler.checkException(jsonObject);
        return Numeric.decodeQuantity(jsonObject.getString(RESULT));
    }

    /**
     * 一般都是 from,to,data
     * @param callParam
     * @return
     */
    public JSONObject ethCall(Map<String,String> callParam) {
        Object[]  params = new Object[2];
        params[0] = callParam;
        params[1] = "latest";
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_CALL.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransctionCount error",throwable);
        }
        return jsonObject;
    }

    public JSONObject poolContent() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.TXPOOL_CONTENT.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("poolContent error",throwable);
        }
        return jsonObject;
    }

//    pending: 10,
//    queued: 7

    public JSONObject poolStatus() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.TXPOOL_STATUS.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("poolStatus error",throwable);
        }
        return jsonObject;
    }


    /**
     * "result": {
     startingBlock: '0x384',
     currentBlock: '0x386',
     highestBlock: '0x454'
     pulledStates: 361434,

     }
     * @return
     */
    public JSONObject getSyncing() {
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_SYNCING.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getSyncing error",throwable);
        }
        return jsonObject;
    }

    public JSONObject removeTx(String tx) {
        Object[]  params = new Object[1];
        params[0] = tx;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.PARITY_REMOVE_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getSyncing error",throwable);
        }
        return jsonObject;
    }


    public JSONObject getTransaction(String tx) {
        Object[]  params = new Object[1];
        params[0] = tx;
        JSONObject jsonObject = null;
        try {
            jsonObject = ethService.callMethod(ETHMethod.ETH_GETTRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getSyncing error",throwable);
        }
        return jsonObject;
    }


}
