package com.zhangsl.wallet.common.eth;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by zhang.shaolong on 2018/4/3.
 */
public class EthConstant {

    public static final BigInteger GAS_LIMIT = new BigInteger("21000");


    public static final BigInteger TOKEN_GAS_LIMIT = new BigInteger("50000");


    public void getGasUse(Web3j web3j, Transaction transaction) {
        EthHandle.getInstance().getTransactionGasLimit(web3j,transaction);
    }


    public static BigDecimal getTokenAmount(String number, int decimals) {
        BigDecimal div = BigDecimal.TEN.pow(decimals);
        return getTokenAmount(new BigDecimal(number), div);
    }

    public static BigInteger etherTokenBigInt(BigDecimal ether,int decimals) {
        BigDecimal mul = BigDecimal.TEN.pow(decimals);
        return ether.multiply(mul).toBigInteger();
    }

    public static BigDecimal getTokenAmount(BigDecimal number, BigDecimal div) {
        return number.divide(div);
    }


}
