package com.zhangsl.wallet.blockcoin.eth.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.filters.Callback;
import org.web3j.protocol.core.filters.Filter;
import org.web3j.protocol.core.filters.FilterException;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthUninstallFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhang.shaolong on 2018/5/4.
 */
public abstract class EthFilter<T> {
    private static final Logger log = LoggerFactory.getLogger(Filter.class);

    final Web3j web3j;

    final Callback<T> callback;

    private volatile BigInteger filterId;

    private ScheduledFuture<?> schedule;

    public EthFilter(Web3j web3j, Callback<T> callback) {
        this.web3j = web3j;
        this.callback = callback;
    }

    public void run(ScheduledExecutorService scheduledExecutorService, long blockTime) {
        try {
            setFilterId();
            getInitialFilterLogs();
            schedule = scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        try {
                            this.pollFilter(filterId);
                        } catch (Throwable e) {
                            setFilterId();
                            log.error("Error sending request", e);
                        }
                    }, 0, blockTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throwException(e);
        }
    }

    private void setFilterId() {
        org.web3j.protocol.core.methods.response.EthFilter ethFilter = null;
        try {
            ethFilter = sendRequest();
            filterId = ethFilter.getFilterId();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ethFilter.hasError()) {
            throwException(ethFilter.getError());
        }
    }

    private void getInitialFilterLogs() {
        try {
            Optional<Request<?, EthLog>> maybeRequest = this.getFilterLogs(this.filterId);
            EthLog ethLog = null;
            if (maybeRequest.isPresent()) {
                ethLog = maybeRequest.get().send();
            } else {
                ethLog = new EthLog();
                ethLog.setResult(Collections.emptyList());
            }
            process(ethLog.getLogs());

        } catch (IOException e) {
            throwException(e);
        }
    }

    private void pollFilter(BigInteger filterId) {
        EthLog ethLog = null;
        try {
            ethLog = web3j.ethGetFilterChanges(filterId).send();
        } catch (IOException e) {
            throwException(e);
        }
        if (ethLog.hasError()) {
            throwException(ethLog.getError());
        } else {
            process(ethLog.getLogs());
        }
    }

    abstract org.web3j.protocol.core.methods.response.EthFilter sendRequest() throws IOException;

    abstract void process(List<EthLog.LogResult> logResults);

    public void cancel() {
        schedule.cancel(false);
        try {
            EthUninstallFilter ethUninstallFilter = web3j.ethUninstallFilter(filterId).send();
            if (ethUninstallFilter.hasError()) {
                throwException(ethUninstallFilter.getError());
            }

            if (!ethUninstallFilter.isUninstalled()) {
                throw new FilterException("Filter with id '" + filterId + "' failed to uninstall");
            }
        } catch (IOException e) {
            throwException(e);
        }
    }


    protected abstract Optional<Request<?, EthLog>> getFilterLogs(BigInteger filterId);

    void throwException(Response.Error error) {
        throw new FilterException("Invalid request: "
                + (error == null ? "Unknown Error" : error.getMessage()));
    }

    void throwException(Throwable cause) {
        throw new FilterException("Error sending request", cause);
    }
}

