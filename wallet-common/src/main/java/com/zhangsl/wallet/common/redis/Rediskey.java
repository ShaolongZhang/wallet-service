package com.zhangsl.wallet.common.redis;

/**
 * Created by zhang.shaolong on 2018/4/5.
 */
public class Rediskey {
//    walletnotify=redis-cli publish bitcoind_walletnotify_pubsub %self

    public static String BTC_NOTIFY_KEY ="bitcoind_blocknotify";

    public static String BTC_WALLET_NOTIFY_KEY ="bitcoind_walletnotify";


    public static String LTC_NOTIFY_KEY ="ltccoind_blocknotify";

    public static String LTC_WALLET_NOTIFY_KEY ="ltccoind_walletnotify";


    public static String BCH_NOTIFY_KEY ="bchcoind_blocknotify";

    public static String BCH_WALLET_NOTIFY_KEY ="bchcoind_walletnotify";

    public static String QTUM_NOTIFY_KEY ="qtumcoind_blocknotify";

    public static String QTUM_WALLET_NOTIFY_KEY ="qtumcoind_walletnotify";


    public static String ETH_WALLET_NOTIFY_KEY ="ethcoind_walletnotify";
}
