package com.zhangsl.wallet.common.eth;

import com.zhangsl.wallet.common.exception.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by zhang.shaolong on 2018/2/25.
 */
public class EthHandle {

    private static final Logger logger = LoggerFactory.getLogger(EthHandle.class);


    private static class EthHandelHolder{
        private static EthHandle ethHandel = new EthHandle();
    }

    private EthHandle(){
    }

    public static EthHandle getInstance() {
        return EthHandle.EthHandelHolder.ethHandel;
    }

    /**
     * 单位是最小单位信息
     * @param address
     * @return
     * @throws Exception
     */
    public BigInteger getAddressBalance(Web3j web3j,String address) {
        try {
            EthGetBalance ethGetBalance = web3j
                    .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            BigInteger wei = ethGetBalance.getBalance();
            return wei;
        } catch (Exception ex) {
            throw new WalletException("get balance error",ex);
        }
    }

    /**
     * 获取矿工费用
     * @return
     * @throws IOException
     */
    public BigInteger getGasPrice(Web3j web3j)  {
        try {
            EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            return ethGasPrice.getGasPrice();
        } catch (Exception ex) {
            throw new WalletException("get gasprice error",ex);
        }

    }


    /**
     * 获取区块的高度信息
     *
     * @return
     * @throws IOException
     */
    public BigInteger getBlock(Web3j web3j) {
        try {
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            return ethBlockNumber.getBlockNumber();
        } catch (Exception ex) {
            throw new WalletException("get block error",ex);
        }

    }




    /**
     * 估计的gasused
     *
     * @param web3j
     * @param transaction
     * @return
     */
    public BigInteger getTransactionGasLimit(Web3j web3j,org.web3j.protocol.core.methods.request.Transaction transaction) {
        BigInteger gasLimit = BigInteger.ZERO;
        try {
            EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
            gasLimit = ethEstimateGas.getAmountUsed();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gasLimit;
    }


    /**
     * 获取地址的交易数信息
     *
     * @param web3j
     * @param address
     * @return
     * @throws Exception
     */
    public BigInteger getNonce(Web3j web3j,String address)  {
        return getNonce(web3j,address,DefaultBlockParameterName.LATEST);
    }

    /**
     * 获取地址的交易数信息
     *
     * @param web3j
     * @param address
     * @return
     * @throws Exception
     */
    public BigInteger getNonce(Web3j web3j,String address,DefaultBlockParameterName defaultBlockParameterName){
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    address, defaultBlockParameterName).sendAsync().get();

            return ethGetTransactionCount.getTransactionCount();
        } catch (Exception ex) {
            throw new WalletException("get nonce error",ex);
        }


    }


    /**
     * 获取交易的详细信息
     *
     * @param web3j
     * @param transactionHash
     * @return
     * @throws Exception
     */
    public Optional<TransactionReceipt> getReceipt(Web3j web3j, String transactionHash)
            throws Exception {
        EthGetTransactionReceipt receipt = web3j
                .ethGetTransactionReceipt(transactionHash)
                .sendAsync()
                .get();
        return receipt.getTransactionReceipt();
    }


    /**
     * 获取交易的详细信息
     *
     * @param web3j
     * @param transactionHash
     * @return
     * @throws Exception
     */
    public Optional<Transaction> getTranscation(Web3j web3j, String transactionHash)
            throws Exception {
        EthTransaction receipt = web3j
                .ethGetTransactionByHash(transactionHash)
                .sendAsync()
                .get();
        return receipt.getTransaction();
    }

    /**
     * @param web3j
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public  String createTransaction(Web3j web3j, String fromAddress, String toAddress, BigInteger amount, BigInteger gasPrice,BigInteger gasLimit) {
        String hexValue = null;
        try {
            BigInteger nonce = EthHandle.getInstance().getNonce(web3j, fromAddress);
            if (gasPrice == null) {
                //默认用以太坊网络的gas
                gasPrice = EthHandle.getInstance().getGasPrice(web3j);
            }
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress,amount);
            byte[] bytes = TransactionEncoder.encode(rawTransaction);
            return Numeric.toHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexValue;
    }

    /**
     * @param web3j
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public RawTransaction createRawTransaction(Web3j web3j, String fromAddress, String toAddress, BigInteger amount, BigInteger gasPrice,BigInteger gasLimit) {
        RawTransaction rawTransaction = null;
        try {
            BigInteger nonce = EthHandle.getInstance().getNonce(web3j, fromAddress,DefaultBlockParameterName.PENDING);
            if (gasPrice == null) {
                //默认用以太坊网络的gas
                gasPrice = EthHandle.getInstance().getGasPrice(web3j);
            }
            rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress,amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawTransaction;
    }



    public static BigDecimal weiToEther(BigInteger wei) {
        return Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
    }

    public static BigInteger etherToWei(BigDecimal ether) {
        return Convert.toWei(ether, Convert.Unit.ETHER).toBigInteger();
    }


}
