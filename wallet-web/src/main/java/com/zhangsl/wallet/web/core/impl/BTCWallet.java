package com.zhangsl.wallet.web.core.impl;

import com.zhangsl.wallet.blockcoin.btc.BitCoinService;
import com.zhangsl.wallet.blockcoin.common.BTCFactory;
import com.zhangsl.wallet.blockcoin.common.BTCHandle;
import com.zhangsl.wallet.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by zhang.shaolong on 2018/4/5.
 */
@Component("BTC")
public class BTCWallet extends BaseBTCWallet {

    private static final Logger logger = LoggerFactory.getLogger("controller");

    /**
     *
     * @param coin
     * @return
     */
    @Override
    public String createTransaction(String coin, String address, String toAddress, BigDecimal amount, BigDecimal gas) {
        BitCoinService bitCoinService = BTCFactory.getBitCoinService(coin.toUpperCase());
        String result = BTCHandle.getInstance().createRawTransaction(bitCoinService,address,toAddress,amount,gas);
//        switch (coin) {
//            case "BTC":
//                result = BTCHandle.getInstance().createRawTransaction(bitCoinService,address,toAddress,amount,gas);
//                break;
//            case "BCH":
//                result = BTCHandle.getInstance().createRawTransaction(bitCoinService,address,toAddress,amount,gas);
//                break;
//            case "LTC":
//                result = BTCHandle.getInstance().createRawTransaction(bitCoinService,address,toAddress,amount,gas);
//                break;
//            case "QTUM":
//                result = BTCHandle.getInstance().createQtumTransaction(bitCoinService,address,toAddress,amount);
//                break;
//            default:
//                break;
//        }
        logger.info(FormatUtils.format("{0} createTransaction result: {1} ",coin,result));
        return result;
    }

}
