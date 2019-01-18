package com.zhangsl.wallet.blockcoin.eth;

/**
 * Created by zhang.shaolong on 2018/4/4.
 */
public enum ETHMethod {

    ETH_GET_TRANSACTIONCOUNT("eth_getTransactionCount"),

    ETH_GASPRICE("eth_gasPrice"),

    ETH_BLOCKNUMBER("eth_blockNumber"),

    ETH_SYNCING("eth_syncing"),

    TXPOOL_STATUS("txpool_status"),

    NET_LISTENING("net_listening"),

    ETH_CALL("eth_call"),

    ETH_SIGN("eth_sign"),

    WEB_SHA3("web3_sha3"),

    TXPOOL_CONTENT("txpool_content"),

    ETH_ESTIMATE_GAS("eth_estimateGas"),

    ETH_SENDRAW_TRNSACTION("eth_sendRawTransaction"),

    ETH_SIGNTRANSACTION("eth_signTransaction"),

    ETH_GETTRANSACTION("eth_getTransaction"),


    ETH_BLOCK_FILTER("eth_newBlockFilter"),

    ETH_GET_FILTER_CHANGES("eth_getFilterChanges"),

    PARITY_REMOVE_TRANSACTION("parity_removeTransaction"),

    ETH_BALANCE("eth_getBalance");

    private String methodName;

    private ETHMethod(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }
}
