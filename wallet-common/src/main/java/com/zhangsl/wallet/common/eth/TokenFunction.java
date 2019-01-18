package com.zhangsl.wallet.common.eth;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * 合约的方法信息，参考信息https://github.com/ethereum/EIPs/blob/master/EIPS/eip-20.md
 *
 * Created by zhang.shaolong on 2018/3/19.
 */
public class TokenFunction {

    /**
     * 获取token地址余额信息
     *
     * @param from
     * @return
     */
    public static Function getBalance(String from) {
        Function function = new Function(EthTokenMethod.TOKEN_BALANCE.getMethod(),
                Arrays.<Type>asList(new Address(from)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return function;
    }

    /**
     * token 币转帐
     *
     * @param from
     * @param to
     * @param value
     * @return
     */
    public static Function transferFrom(String from,String to,BigInteger value) {
        Function function = new Function(
                EthTokenMethod.TOKEN_TRANSFER_FROM.getMethod(),
                Arrays.<Type>asList(new Address(from),
                        new Address(to),
                        new Uint256(value)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }


    /**
     * token 币转帐
     * @param to
     * @param value
     * @return
     */
    public static Function transfer(String to, BigInteger value) {
        Function function = new Function(
                EthTokenMethod.TOKEN_TRANSFER.getMethod(),
                Arrays.<Type>asList(new Address(to),
                        new Uint256(value)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return function;
    }

    /**
     * 获取代币成名
     * @return
     */
    public static Function getName() {
        Function function = new Function(
                EthTokenMethod.TOKEN_NAME.getMethod(),
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }


    /**
     * 获取代币符号
     * @return
     */
    public static Function getSymbol() {
        Function function = new Function(
                EthTokenMethod.TOKEN_SYMBOL.getMethod(),
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return function;
    }

    /**
     * 获取代币精度
     *
     * @return
     */
    public static Function getdecimals() {
        Function function = new Function(
                EthTokenMethod.TOKEN_DECIMALS.getMethod(),
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return function;
    }

    /**
     * 转帐事件信息
     * @return
     */
    public static Event transferEvent () {
         Event event = new Event(EthTokenMethod.TOKEN_TRANSFER_EVENT.getMethod(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
         return event;
    }


    /**
     * eos映射时间信息
     * @param key
     * @return
     */
    public static Function eosRegister(String key) {
        Function function = new Function(
                "register",
                Arrays.<Type>asList(new Utf8String(key)),
                Collections.<TypeReference<?>>emptyList());
        return function;
    }
}
