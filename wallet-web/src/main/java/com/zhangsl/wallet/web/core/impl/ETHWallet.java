package com.zhangsl.wallet.web.core.impl;

import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.exception.WalletException;
import com.zhangsl.wallet.web.bean.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * 以太坊的转帐信息
 */
@Component("ETH")
public class ETHWallet extends BaseEthWallet {

    private static final Logger logger = LoggerFactory.getLogger("controller");


    @Override
    public TransactionMessage getTransaction(String coin, String transactionId) {
        Web3j web3j = this.web3j;
        try {
            //走以太坊节点查询收据信息
            Optional<Transaction> receipt = EthHandle.getInstance().getTranscation(web3j, transactionId);
            Transaction transactionReceipt = receipt.get();
            if (transactionReceipt == null) {
                //直接走网络请求查询数据信息
                return null;
            }
            TransactionMessage transaction = new TransactionMessage();
            transaction.setFrom(transactionReceipt.getFrom());
            transaction.setTo(transactionReceipt.getTo());
            transaction.setTransaction(transactionReceipt.getHash());
            transaction.setAmout(EthHandle.weiToEther(transactionReceipt.getValue()));
            transaction.setGasPrice(EthHandle.weiToEther(transactionReceipt.getGasPrice()));
            return transaction;
        } catch (Exception e) {
            logger.error("getTransaction error ", e);
            throw new WalletException("getTransaction error");
        }
    }

    @Override
    public String createTransaction(String coin, String address, String toAddress, BigDecimal amount, BigDecimal gas) {
        if (!coin.equalsIgnoreCase(CoinType.CoinEnum.ETH.toString())) {
            throw new WalletException("coin error");
        }
        Web3j web3j = this.web3j;
        BigInteger amountWei = EthHandle.etherToWei(amount);
        //校验余额信息
        BigInteger balance = ethCoinService.getBalance(address);

        if (balance.compareTo(amountWei) == -1) {
            throw new WalletException(ErrorCode.BALCNACE_ERROR);
        }
        BigInteger gasInteger = null;
        if (gas != null) {
            gasInteger = EthHandle.etherToWei(gas);
        } else {
            //默认用以太坊网络的gas
            gasInteger = EthHandle.getInstance().getGasPrice(web3j);
            //矿工费增加
            gasInteger = gasInteger.multiply(BigInteger.valueOf(2));
        }
        //新加密
        return rawTransaction(address, toAddress, "", amountWei, gasInteger,balance);

    }

    @Override
    public BigDecimal queryBalance(String coin,String address) {
        if (!coin.equalsIgnoreCase(CoinType.CoinEnum.ETH.toString())) {
            throw new WalletException("coin error");
        }
        BigInteger bigIntegerbalance = ethCoinService.getBalance(address);
        BigDecimal balance = EthHandle.weiToEther(bigIntegerbalance);
        return balance;
    }

}
