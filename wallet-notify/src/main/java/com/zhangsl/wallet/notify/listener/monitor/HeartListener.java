package com.zhangsl.wallet.notify.listener.monitor;

import rx.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 一直检测监听是否存活,防止监听失败了 没有监听
 */
public class HeartListener {

    private ScheduledExecutorService scheduledExecutorService;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private Subscription subscription;


    public HeartListener(Subscription subscription) {
        this.subscription = subscription;
        init();
    }

    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                heart();
            }

        }, 1, 60, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                do {
                    scheduledExecutorService.shutdownNow();
                } while (!scheduledExecutorService.isTerminated());
            }
        });
    }

    private void heart() {
        if (subscription != null) {
            boolean unsubscribed = subscription.isUnsubscribed();
            if (unsubscribed) {
                //TODO 自动断开订阅了,报警重新链接
            }
        }
    }
}
