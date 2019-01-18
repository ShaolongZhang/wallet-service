package com.zhangsl.wallet.common.util;

import com.google.common.util.concurrent.CycleDetectingLockFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhang.shaolong on 2018/4/26.
 */
public class WalletLock {

    public static CycleDetectingLockFactory factory;


    static {
        setPolicy(CycleDetectingLockFactory.Policies.THROW);
    }

    private static void setPolicy(CycleDetectingLockFactory.Policy policy) {
        factory = CycleDetectingLockFactory.newInstance(policy);
    }

    public static ReentrantLock lock(String name) {
        return factory.newReentrantLock(name);
    }

}
