package com.zhangsl.wallet.blockcoin.eth.filter;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * Created by zhang.shaolong on 2018/5/4.
 */
public class JsonRpcEth  {

    private final Web3j web3j;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Scheduler scheduler;

    public JsonRpcEth(Web3j web3j, ScheduledExecutorService scheduledExecutorService) {
        this.web3j = web3j;
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduler = Schedulers.from(scheduledExecutorService);
    }

    public Observable<String> ethBlockHashObservable(long pollingInterval) {
        return Observable.create(subscriber -> {
            EthBlockFilter blockFilter = new EthBlockFilter(
                    web3j, subscriber::onNext);
            run(blockFilter, subscriber, pollingInterval);
        });
    }


    public Observable<Log> ethLogObservable(
            org.web3j.protocol.core.methods.request.EthFilter ethFilter, long pollingInterval) {
        return Observable.create((Subscriber<? super Log> subscriber) -> {
            EthLogFilter logFilter = new EthLogFilter(
                    web3j, subscriber::onNext, ethFilter);
            run(logFilter, subscriber, pollingInterval);
        });
    }

    private <T> void run(
            EthFilter<T> filter, Subscriber<? super T> subscriber,
            long pollingInterval) {

        filter.run(scheduledExecutorService, pollingInterval);
        subscriber.add(Subscriptions.create(filter::cancel));
    }

    public Observable<Transaction> transactionObservable(long pollingInterval) {
        return blockObservable(true, pollingInterval)
                .flatMapIterable(JsonRpcEth::toTransactions);
    }


    public Observable<EthBlock> blockObservable(
            boolean fullTransactionObjects, long pollingInterval) {
        return ethBlockHashObservable(pollingInterval)
                .flatMap(blockHash ->
                        web3j.ethGetBlockByHash(blockHash, fullTransactionObjects).observable());
    }


    private static List<Transaction> toTransactions(EthBlock ethBlock) {
        // If you ever see an exception thrown here, it's probably due to an incomplete chain in
        // Geth/Parity. You should resync to solve.
        return ethBlock.getBlock().getTransactions().stream()
                .map(transactionResult -> (Transaction) transactionResult.get())
                .collect(Collectors.toList());
    }
}
