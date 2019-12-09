package com.zhangsl.wallet.blockcoin.common;

import com.zhangsl.wallet.blockcoin.eth.filter.JsonRpcWeb3j;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.exception.WalletException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Async;

import java.io.IOException;

/**
 *
 * 以太坊钱web3j包地址信息
 * Created by zhang.shaolong on 2018/2/24.
 */
public class EthWeb3jClient {

    /**  web3j 链接以太坊 ***/
    private Web3j web3j;

    private static class WalletClientHolder{
        private static EthWeb3jClient walletClient = new EthWeb3jClient();
    }

    private EthWeb3jClient(){
        init();
    }

    public static EthWeb3jClient getInstance() {
        return WalletClientHolder.walletClient;
    }


    /**
     * 初始化client信息
     */
    private synchronized  void init() {
        web3j = Web3j.build(new HttpService(BlockProperties.getUrl(CoinType.CoinEnum.ETH.toString())));
    }

    public Web3j getClient() {
        return web3j;
    }


    public Web3j getClient(long time) {
        return new JsonRpcWeb3j(new HttpService(BlockProperties.getUrl(CoinType.CoinEnum.ETH.toString())),time, Async.defaultExecutorService());
    }


    public Web3j getClient(String url,long time) {
        return new JsonRpcWeb3j(new HttpService(url),time, Async.defaultExecutorService());
    }


    public String getVersion() {
        if (web3j != null) {
            throw new WalletException("error");
        }
        try {
            return web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
