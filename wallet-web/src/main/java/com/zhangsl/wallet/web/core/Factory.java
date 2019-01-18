package com.zhangsl.wallet.web.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

@Component
public class Factory extends ApplicationObjectSupport {

    public Wallet getWallet(String currencyName) {
        ApplicationContext ctx = getApplicationContext();
        return ctx.getBean(currencyName, Wallet.class);
    }
}
