package com.zhangsl.wallet.blockcoin.btc;

/**
 * Created by zhang.shaolong on 2018/4/4.
 */
public enum BTCMethod {

    //btc ltc rpc methods
    GET_BALANCE("getbalance"),

    PING("ping"),

    GET_INFO("getinfo"),

    GET_NEW_ADDRESS("getnewaddress"),

    DUMP_PRIVATE_KEY("dumpprivkey"),

    SEND_RAW_TRANSACTION("sendrawtransaction"),

    SEND_FROM("sendfrom"),

    SEND_TO_ADDRESS("sendtoaddress"),

    CREATE_TRANSCATION("createtranscation"),

    CREATE_RAW_TRANSACTION("createrawtransaction"),

    GET_RAW_TRANSACTION("getrawtransaction"),

    GET_TRANSACTION("gettransaction"),

    GET_BLOCK("getblock"),

    DECODE_RAW_TRANSACTION("decoderawtransaction"),

    VALIDATEADDRESS("validateaddress"),

    LIST_UNSPENT("listunspent"),

    FUND_RAW_TRANSACTION("fundrawtransaction"),

    GET_WALLET_INFO("getwalletinfo"),

    GET_BLOCK_INFO("getblockchaininfo"),

    /** estimatefee  **/
    ESTIMATES_MARTFEE("estimatesmartfee"),

    DECODE_SCRIPT("decodescript"),

    SIGN_RAW_TRANSACTION("signrawtransaction");

    private String methodName;

    private BTCMethod(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }
}
