package com.zhangsl.wallet.common.eth;

import org.web3j.abi.*;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 专门处理eth token币
 * Created by zhang.shaolong on 2018/3/19.
 */
public class EthTokenHandle {

    private static final String DEFAULT_ADDRESS = "0x0000000000000000000000000000000000000000";

    private static class EthTokenHandleHolder{
        private static EthTokenHandle ethTokenHandle = new EthTokenHandle();
    }

    private EthTokenHandle(){
    }

    public static EthTokenHandle getInstance() {
        return EthTokenHandleHolder.ethTokenHandle;
    }

    /**
     * 查询代币的余额信息
     *
     * @param web3j
     * @param fromAddress  //用户地址
     * @param contractAddress  //合约地址
     * @return
     */
    public  BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {
        Function function = TokenFunction.getBalance(fromAddress);
        String data = FunctionEncoder.encode(TokenFunction.getBalance(fromAddress));
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            balanceValue = (BigInteger) results.get(0).getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue;
    }

    /**
     * 查询代币名称
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public  String getTokenName(Web3j web3j, String contractAddress) {
        String name = null;
        Function function = TokenFunction.getName();
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(DEFAULT_ADDRESS, contractAddress, data);
        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            name = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 查询代币符号
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public  String getTokenSymbol(Web3j web3j, String contractAddress) {
        String symbol = null;
        Function function = TokenFunction.getSymbol();
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(DEFAULT_ADDRESS, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    /**
     * 查询代币精度
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public  int getTokenDecimals(Web3j web3j, String contractAddress) {
        int decimal = 0;
        Function function = TokenFunction.getdecimals();
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(DEFAULT_ADDRESS, contractAddress, data);
        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            decimal = Integer.parseInt(results.get(0).getValue().toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return decimal;
    }


    /**
     * token币转帐，https://web3j.readthedocs.io/en/latest/transactions.html#transacting-with-contract
     *
     * @param web3j
     * @param fromAddress
     * @param toAddress
     * @param contractAddress
     * @param amount
     * @return
     */
    public  String createTokenTransaction(Web3j web3j, String fromAddress, String toAddress, String contractAddress, BigInteger amount, BigInteger gasPrice,BigInteger gasLimit) {
        String hexValue = null;
        try {
            Function function = TokenFunction.transfer(toAddress, amount);
            String data = FunctionEncoder.encode(function);
            BigInteger nonce = EthHandle.getInstance().getNonce(web3j, fromAddress,DefaultBlockParameterName.PENDING);
            if (gasPrice == null) {
                //默认用以太坊网络的gas
                gasPrice = EthHandle.getInstance().getGasPrice(web3j);
            }
            //合约发送信息
            RawTransaction rawTransaction = signTransaction(nonce, gasPrice, gasLimit, contractAddress, data);
            byte[] bytes = TransactionEncoder.encode(rawTransaction);
            return Numeric.toHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexValue;
    }


    public  RawTransaction createTokenContract(Web3j web3j, String fromAddress, BigInteger gasPrice,BigInteger gasLimit,String data) {
        RawTransaction rawTransaction = null;
        try {
            BigInteger nonce = EthHandle.getInstance().getNonce(web3j, fromAddress,DefaultBlockParameterName.PENDING);
            if (gasPrice == null) {
                //默认用以太坊网络的gas
                gasPrice = EthHandle.getInstance().getGasPrice(web3j);
            }
            //合约发送信息
            rawTransaction = RawTransaction.createContractTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, "0x"+data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawTransaction;
    }


    public  RawTransaction createTokenRawTransaction(Web3j web3j, String fromAddress, String toAddress, String contractAddress, BigInteger amount, BigInteger gasPrice,BigInteger gasLimit) {
        RawTransaction rawTransaction = null;
        try {
            Function function = TokenFunction.transfer(toAddress, amount);
            String data = FunctionEncoder.encode(function);
            BigInteger nonce = EthHandle.getInstance().getNonce(web3j, fromAddress,DefaultBlockParameterName.PENDING);
            if (gasPrice == null) {
                //默认用以太坊网络的gas
                gasPrice = EthHandle.getInstance().getGasPrice(web3j);
            }
            //合约发送信息
            rawTransaction = signTransaction(nonce, gasPrice, gasLimit, contractAddress, "0x"+data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawTransaction;
    }

    /**
     * token币转帐，https://web3j.readthedocs.io/en/latest/transactions.html#transacting-with-contract
     *
     * @param web3j
     * @param fromAddress
     * @param toAddress
     * @param contractAddress
     * @param amount
     * @return
     */
    public  String sendTokenTransaction(Web3j web3j, String fromAddress, String toAddress, String contractAddress, BigInteger amount, BigInteger gasPrice,BigInteger gasLimit) {
        String txHash = null;
        try {
            String hexValue = createTokenTransaction( web3j, fromAddress,  toAddress, contractAddress, amount, gasPrice,gasLimit);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            return ethSendTransaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txHash;
    }

    private  RawTransaction signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, String data) throws IOException {
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                data);
        return rawTransaction;
    }


    public List<EventValues> extractEventLogs(List<Log> logs) {
        Event event = TokenFunction.transferEvent();
        List<EventValues> values = new ArrayList<>();
        for (Log log : logs) {
            EventValues eventValues = extractEventParameters(event, log);
            if (eventValues != null) {
                values.add(eventValues);
            }
        }
        return values;
    }

    public EventValues extractEventLog(Log log) {
        Event event = TokenFunction.transferEvent();
        EventValues eventValues = extractEventParameters(event, log);
        return eventValues;
    }

    private EventValues extractEventParameters(
            Event event, Log log) {
        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }
}
