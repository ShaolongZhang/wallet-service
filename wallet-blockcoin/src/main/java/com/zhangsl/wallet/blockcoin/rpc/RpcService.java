package com.zhangsl.wallet.blockcoin.rpc;

import com.zhangsl.wallet.blockcoin.common.BlockProperties;
import com.zhangsl.wallet.common.coin.CoinType;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/3/13.
 */
public  class  RpcService {

    public static JSONRPCService buildBTCService(RestTemplate restTemplate) {
        //比特币默认的头信息添加
        Map<String,String> header = new HashMap<String,String>();
        //头文件添加的信息，这个还不确定是添加什么
        header.put("Authorization", "Basic " + Base64.encodeBase64String(getRpcUser()));
        return new JSONRPCService(BlockProperties.getUrl(CoinType.CoinEnum.BTC.toString()),restTemplate,header);
    }

    public static JSONRPCService buildLtcService(RestTemplate restTemplate) {
        //莱特币默认的头信息添加
        Map<String,String> header = new HashMap<String,String>();
        header.put("Authorization", "Basic " + Base64.encodeBase64String(getRpcUser()));
        return new JSONRPCService(BlockProperties.getUrl(CoinType.CoinEnum.LTC.toString()),restTemplate,header);
    }

    public static JSONRPCService buildBchService(RestTemplate restTemplate) {
        //莱特币默认的头信息添加
        Map<String,String> header = new HashMap<String,String>();
        header.put("Authorization", "Basic " + Base64.encodeBase64String(getRpcUser()));
        return new JSONRPCService(BlockProperties.getUrl(CoinType.CoinEnum.BCH.toString()),restTemplate,header);
    }

    public static JSONRPCService buildQtumService(RestTemplate restTemplate) {
        //量子的默认的头信息添加
        Map<String,String> header = new HashMap<String,String>();
        header.put("Authorization", "Basic " + Base64.encodeBase64String(getRpcUser()));
        return new JSONRPCService(BlockProperties.getUrl(CoinType.CoinEnum.QTUM.toString()),restTemplate,header);
    }

    private static byte[] getRpcUser() {
       return  BlockProperties.getRpcUser().getBytes();
    }


    //以太坊的rpc信息，可以调用
    public static JSONRPCService buildETHService(RestTemplate restTemplate) {
        return new JSONRPCService(BlockProperties.getUrl(CoinType.CoinEnum.ETH.toString()),restTemplate);
    }

    public static JSONRPCService buildService(String url, Map<String,String> header,RestTemplate restTemplate) {
        return new JSONRPCService(url,restTemplate,header);
    }
}
