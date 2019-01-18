package com.zhangsl.wallet.notify.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */

@Component
public class NotifyConfirmApplication {

    //btc 确认数
    @Value("${coin.receive.btc}")
    private String btc;

    //eth 确认数
    @Value("${coin.receive.eth}")
    private String eth;

    //btc 确认数
    @Value("${coin.receive.ltc}")
    private String ltc;

    //eth 确认数
    @Value("${coin.receive.bch}")
    private String bch;

    //tb 确认数
    @Value("${coin.receive.tb}")
    private String tb;


    //eos 确认数
    @Value("${coin.receive.eos}")
    private String eos;

    //usdt 确认数
    @Value("${coin.receive.usdt}")
    private String usdt;


    public long getConfirm(String coin) {
        switch (coin) {
            case "BTC":
                return Long.valueOf(btc);
            case "LTC":
                return Long.valueOf(ltc);
            case "BCH":
                return Long.valueOf(bch);
            case "ETH":
                return Long.valueOf(eth);
            case "TB":
                return Long.valueOf(tb);
            case "EOS":
                return Long.valueOf(eos);
            case "USDT":
                return Long.valueOf(usdt);
            default:
                return 6L;
        }
    }

}
