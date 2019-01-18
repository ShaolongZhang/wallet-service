package com.zhangsl.wallet.blockcoin.btc;


import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.rpc.JSONRPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 参考文档信息 https://en.bitcoin.it/wiki/Original_Bitcoin_client/API_calls_list，莱特币和比特币一样
 *
 * Created by zhang.shaolong on 2018/3/23.
 */
public class BitCoinService {

    private static final Logger logger = LoggerFactory.getLogger(BitCoinService.class);


    private JSONRPCService jsonrpcService;

    private BitCoinService(JSONRPCService jsonrpcService) {
        this.jsonrpcService = jsonrpcService;
    }

    public static BitCoinService buildService(JSONRPCService jsonrpcService) {
         return new BitCoinService(jsonrpcService);
    }

    private final static String RESULT = "result";


    public JSONObject ping()  {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.PING.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("ping error",throwable);
        }
        return jsonObject;
    }

    /**
     * 创建交易信息，返回hash 值 ,发送格式：需要计算好找零钱的信息
     *
     * '[{"txid" : "5d2abd13a6ee2d3ffb1758259eb5d0ffece91a86c0670f1e6c472618995a420c", "vout" : 1}]' '{"mj7AFvkxFEvxmGwwP5jgXRtyzhPAhZs2gQ": 0.0005, "my1kprdpM17PZJnB55GwJ5AzLGjx9isHh9": 0.000045}'
     * @param prevOut
     * @param out
     * @return
     */
    public JSONObject createRawTransaction(Object[] prevOut, Object out) {
        Object[]  params = new Object[2];
        params[0] = prevOut;
        params[1] = out;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.CREATE_RAW_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("createRawTransaction error",throwable);
        }
        return jsonObject;
    }

    public JSONObject fundRawTransaction(String hashTranscation, Object out) {
        Object[]  params = new Object[2];
        params[0] = hashTranscation;
        params[1] = out;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.FUND_RAW_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("fundRawTransaction error",throwable);
        }
        return jsonObject;
    }

    public  JSONObject getNewAddress() {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_NEW_ADDRESS.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getNewAddress error",throwable);
        }
        return jsonObject;
    }

    public  JSONObject getInfo() {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_INFO.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getInfo error",throwable);
        }
        return jsonObject;
    }

    /**
     *   {
     "walletname": "wallet.dat",
     "walletversion": 159900,
     "balance": 0.00000000,
     "unconfirmed_balance": 0.00000000,
     "immature_balance": 0.00000000,
     "txcount": 6,
     "keypoololdest": 1523720605,
     "keypoolsize": 1000,
     "keypoolsize_hd_internal": 1000,
     "paytxfee": 0.00000000,
     "hdmasterkeyid": "ee196b6a1cab6c7fb9f31fbadbd58f3f39b80fd5"
     }
     * @return
     */
    public JSONObject getWwalletInfo() {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_WALLET_INFO.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getInfo error",throwable);
        }
        return jsonObject;
    }


    /**
     *   "chain": "test",
     *    "blocks": 1293114,
     *    "headers": 1293114,
     * @return
     */
    public JSONObject getBlockInfo() {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_BLOCK_INFO.toString(), null,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getInfo error",throwable);
        }
        return jsonObject;
    }



    /**
     * 地址私钥
     * @param address
     * @return
     */
    public  JSONObject dumpPrivateKey(String address) {
        Object[]  params = new Object[1];
        params[0] = address;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.DUMP_PRIVATE_KEY.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("dumpPrivateKey error",throwable);
        }
        return jsonObject;
    }

    /**
     * 发送转帐，获取txid值信息
     * @param fromAccount
     * @param toAddress
     * @param amount
     * @return
     */
    public  JSONObject sendFrom(String fromAccount, String toAddress, BigDecimal amount)  {
        Object[]  params = new Object[3];
        params[0] = fromAccount;
        params[1] = toAddress;
        params[2] = amount;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.SEND_FROM.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("sendFrom error",throwable);
        }
        return jsonObject;
    }

    /**
     * 获取交易详情信息
     *
     * @param txid
     * @return
     */
    public JSONObject getTransaction(String txid)  {
        Object[]  params = new Object[2];
        params[0] = txid;
        params[1] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getTransaction error",throwable);
        }
        return jsonObject;
    }


    /**
     * 获取交易详情信息
     *
     * @param blackhx
     * @return
     */
    public JSONObject getBlock(String blackhx)  {
        Object[]  params = new Object[1];
        params[0] = blackhx;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_BLOCK.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getBlock error",throwable);
        }
        return jsonObject;
    }


    /**
     * 获取交易详情信息
     *
     * @param txid
     * @return
     */
    public JSONObject getRawTransaction(String txid)  {
        Object[]  params = new Object[2];
        params[0] = txid;
        params[1] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_RAW_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getRawTransaction error",throwable);
        }
        return jsonObject;
    }


    public JSONObject getRawTransactionJson(String txid)  {
        Object[]  params = new Object[2];
        params[0] = txid;
        params[1] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_RAW_TRANSACTION.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getRawTransaction error",throwable);
        }
        return jsonObject;
    }


    public  JSONObject getBalance(String account,int confrim) {
        Object[] params = new Object[3];
        params[0] = account;
        params[1] = confrim;
        params[2] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_BALANCE.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getBalance error",throwable);
        }
        return jsonObject;
    }

    /**
     * 查询整个钱包的地址信息
     *
     * @return
     */
    public JSONObject getBalance() {
        Object[] params = new Object[3];
        params[0] = "";
        params[1] = 6;
        params[2] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.GET_BALANCE.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("getBalance error", throwable);
        }
        return jsonObject;
    }



    /**
     * 解密交易信息
     *
     * @param hex
     * @return
     */
    public JSONObject decodeRawTransaction(String hex)  {
        Object[] params = new Object[1];
        params[0] = hex;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.DECODE_RAW_TRANSACTION.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("decodeRawTransaction error",throwable);
        }
        return jsonObject;
    }


    /**
     * 首先发送一笔交易信息，获取到txid信息
     *
     * @param toAddress
     * @param amount
     * @return
     */
    public JSONObject sendToAddress(String toAddress, BigDecimal amount)  {
        Object[] params = new Object[2];
        params[0] = toAddress;
        params[1] = amount;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.SEND_TO_ADDRESS.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("sendToAddress error",throwable);
        }
        return jsonObject;
    }

    /**
     * 交易签名
     * @param hexString
     * @return
     */
    public JSONObject signRawTransaction(String hexString)  {
        Object[] params = new Object[1];
        params[0] = hexString;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.SIGN_RAW_TRANSACTION.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("signRawTransaction error",throwable);
        }
        return jsonObject;
    }

    /**
     * 发送签名后台的交易信息
     *
     * @param hexString
     * @return
     */
    public JSONObject sendRawTransaction(String hexString)  {
        Object[] params = new Object[1];
        params[0] = hexString;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.SEND_RAW_TRANSACTION.toString(), params, JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("sendRawTransaction error",throwable);
        }
        return jsonObject;
    }

    /**
     *  地址公钥
     *    "pubkey": "02b52d2cdb7548b756ffa10b43a7af12b3c08502d31b715ec47914a21ef316222c"
     * @param address
     * @return
     */
    public  JSONObject validateAddress(String address)  {
        Object[]  params = new Object[1];
        params[0] = address;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.VALIDATEADDRESS.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("validateAddress error",throwable);
        }
        return jsonObject;
    }


    public JSONObject listUnspent(int minconf)  {
        Object[]  params = new Object[1];
        params[0] = minconf;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.LIST_UNSPENT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }

    public JSONObject listUnspent(int minconf, int maxconf) {
        Object[]  params = new Object[2];
        params[0] = minconf;
        params[1] = maxconf;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.LIST_UNSPENT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }


    public JSONObject listUnspent(int minconf, int maxconf, List<String> address)  {
        Object[]  params = new Object[3];
        params[0] = minconf;
        params[1] = maxconf;
        params[2] = address;
        params[3] = true;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.LIST_UNSPENT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }

    /**
     * object     {
     "minimumAmount"    (numeric or string, default=0) Minimum value of each UTXO in BTC
     "maximumAmount"    (numeric or string, default=unlimited) Maximum value of each UTXO in BTC
     "maximumCount"     (numeric or string, default=unlimited) Maximum number of UTXOs
     "minimumSumAmount" (numeric or string, default=unlimited) Minimum sum value of all UTXOs in BTC
     }
     * @param minconf
     * @param maxconf
     * @param address
     * @param object
     * @return
     */
    public JSONObject listUnspent(int minconf, int maxconf, List<String> address,Object object)  {
        Object[]  params = new Object[5];
        params[0] = minconf;
        params[1] = maxconf;
        params[2] = address;
        params[3] = true;
        params[4] = object;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.LIST_UNSPENT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }

    /**
     * 获取费率信息
     * @param minconf
     * @return
     */
    public JSONObject estimatesmartfee(int minconf)  {
        Object[]  params = new Object[1];
        params[0] = minconf;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.ESTIMATES_MARTFEE.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }

    public JSONObject decodeScript(String hex)  {
        Object[]  params = new Object[1];
        params[0] = hex;
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonrpcService.callMethod(BTCMethod.DECODE_SCRIPT.toString(), params,JSONObject.class);
        } catch (Throwable throwable) {
            logger.error("listUnspent error",throwable);
        }
        return jsonObject;
    }
}
