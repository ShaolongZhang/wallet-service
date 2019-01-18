package com.zhangsl.wallet.common.coin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于以太坊的合约信息
 * Created by zhang.shaolong on 2018/3/16.
 */
public enum EthToken {



    EOS_TOKEN(CoinType.CoinEnum.EOS,"0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0"),

    USDT_TOKEN(CoinType.CoinEnum.USDT,"0xdac17f958d2ee523a2206206994597c13d831ec7"),

    //测试用的
    BT_TOKEN(CoinType.CoinEnum.TB,"0xda7d4ebf449b971f7e6e3df55fde8fe4327c8a15");

    //合约地址信息
    private String contract;

    private CoinType.CoinEnum name;

    EthToken(CoinType.CoinEnum name, String contract) {
        this.name = name;
        this.contract = contract;
    }

    public String getContract() {
        return contract;
    }

    public CoinType.CoinEnum getName() {
        return name;
    }

    public static EthToken getTokenByCoin(String coinType) {
        for(EthToken token : EthToken.values()) {
            if (token.getName().toString().equalsIgnoreCase(coinType)) {
                return token;
            }
        }
        return null;
    }

    public static List<String> tokenAddress = new ArrayList<String>();

    public static Map<String,CoinType.CoinEnum> tokenMap = new HashMap<String,CoinType.CoinEnum>();


    static {
       for(EthToken token : EthToken.values()) {
           tokenAddress.add(token.contract);
           tokenMap.put(token.contract,token.name);
       }
    }
}
