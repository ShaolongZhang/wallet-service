package com.zhangsl.wallet.blockcoin.common;

import com.zhangsl.wallet.blockcoin.btc.BitCoinService;
import com.zhangsl.wallet.blockcoin.rpc.RpcService;
import com.zhangsl.wallet.common.exception.WalletException;
import org.springframework.web.client.RestTemplate;

/**
 * 用默认的时间设置信息
 * Created by zhang.shaolong on 2018/4/8.
 */
public class BTCFactory {


    private static BitCoinService bitCoinService = BitCoinService.buildService(RpcService.buildBTCService(new RestTemplate(BlockProperties.create())));


    private static BitCoinService ltcCoinService = BitCoinService.buildService(RpcService.buildLtcService(new RestTemplate(BlockProperties.create())));


    private static BitCoinService bchCoinService = BitCoinService.buildService(RpcService.buildBchService(new RestTemplate(BlockProperties.create())));

    private static BitCoinService qtumCoinService = BitCoinService.buildService(RpcService.buildQtumService(new RestTemplate(BlockProperties.create())));


    public static BitCoinService getBitCoinService(String coin) {
        switch (coin) {
            case "BTC":
                return bitCoinService;
            case "LTC":
                return ltcCoinService;
            case "BCH":
                return bchCoinService;
            case "QTUM":
                return qtumCoinService;
            default:
                throw new WalletException("no suport rpc service");
        }
    }
}
