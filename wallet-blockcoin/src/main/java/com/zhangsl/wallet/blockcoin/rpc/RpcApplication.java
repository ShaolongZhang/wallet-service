package com.zhangsl.wallet.blockcoin.rpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */
@Configuration
@PropertySource("classpath:blockcoin.properties")
public class RpcApplication {

    //btc 的地址信息
    @Value("${service.rpc.btc}")
    private String btc;

    //eth 的地址信息
    @Value("${service.rpc.eth}")
    private String eth;

    public String getBtc() {
        return btc;
    }

    public String getEth() {
        return eth;
    }
}
