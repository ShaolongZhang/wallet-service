package com.zhangsl.wallet.blockcoin.eth.filter;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.filters.Callback;
import org.web3j.protocol.core.filters.FilterException;
import org.web3j.protocol.core.methods.response.EthLog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Created by zhang.shaolong on 2018/5/4.
 */
public class EthBlockFilter extends EthFilter<String> {

    public EthBlockFilter(Web3j web3j, Callback<String> callback) {
        super(web3j, callback);
    }

    @Override
    org.web3j.protocol.core.methods.response.EthFilter sendRequest() throws IOException {
        return web3j.ethNewBlockFilter().send();
    }

    @Override
    void process(List<EthLog.LogResult> logResults) {
        for (EthLog.LogResult logResult : logResults) {
            if (logResult instanceof EthLog.Hash) {
                String blockHash = ((EthLog.Hash) logResult).get();
                callback.onEvent(blockHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
    }

    @Override
    protected Optional<Request<?, EthLog>> getFilterLogs(BigInteger filterId) {
        return Optional.empty();
    }


}
