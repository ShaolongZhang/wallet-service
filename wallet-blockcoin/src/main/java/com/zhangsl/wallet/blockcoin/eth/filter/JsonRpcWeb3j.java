package com.zhangsl.wallet.blockcoin.eth.filter;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import rx.Observable;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zhang.shaolong on 2018/5/4.
 */
public class JsonRpcWeb3j extends JsonRpc2_0Web3j {

    private  JsonRpcEth jsonRpcEth;

    private  long blockTime;

    private ScheduledExecutorService scheduledExecutorService;

    public JsonRpcWeb3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpcWeb3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
        this.jsonRpcEth = new JsonRpcEth(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
        this.scheduledExecutorService = scheduledExecutorService;
    }



    @Override
    public Observable<EthBlock> blockObservable(boolean fullTransactionObjects) {
        return jsonRpcEth.blockObservable(fullTransactionObjects, blockTime);
    }

    @Override
    public Observable<Log> ethLogObservable(
            org.web3j.protocol.core.methods.request.EthFilter ethFilter) {
        return jsonRpcEth.ethLogObservable(ethFilter, blockTime);
    }

    @Override
    public Observable<org.web3j.protocol.core.methods.response.Transaction> transactionObservable() {
        return jsonRpcEth.transactionObservable(blockTime);
    }
}
