package com.zhangsl.wallet.web.core.impl;

import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.coin.EthToken;
import com.zhangsl.wallet.common.eth.EthConstant;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.eth.EthTokenHandle;
import com.zhangsl.wallet.common.exception.WalletException;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.web.bean.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.web3j.utils.Convert.fromWei;

/**
 * Created by zhang.shaolong on 2018/3/16.
 */
@Component("ERC20TOKEN")
public class ETHTokenWallet extends BaseEthWallet {

    private static final Logger logger = LoggerFactory.getLogger("controller");

    @Override
    public TransactionMessage getTransaction(String coin, String transactionId) {
        Web3j web3j = this.web3j;
        try {
            //走以太坊节点查询收据信息
            Optional<TransactionReceipt> receipt = EthHandle.getInstance().getReceipt(web3j, transactionId);
            TransactionReceipt transactionReceipt = receipt.get();
            if (transactionReceipt == null) {
                //直接走网络请求查询数据信息
                return null;
            }
            TransactionMessage transaction = new TransactionMessage();
            transaction.setTransaction(transactionReceipt.getTransactionHash());
            EventValues eventValues = EthTokenHandle.getInstance().extractEventLog(transactionReceipt.getLogs().get(0));
            if (eventValues == null) {
                return null;
            }
            Address from = (Address) eventValues.getIndexedValues().get(0); //转的币
            Address to = (Address) eventValues.getIndexedValues().get(1);
            transaction.setFrom(from.toString());
            transaction.setTo(to.toString());
            Uint256 value = (Uint256) eventValues.getNonIndexedValues().get(0);
            BigDecimal amout = fromWei(value.getValue().toString(), Convert.Unit.ETHER);
            transaction.setAmout(amout);
            transaction.setGasPrice(EthHandle.weiToEther(transactionReceipt.getGasUsed()));
            return transaction;
        } catch (Exception e) {
            logger.error("getTransaction error ", e);
            throw new WalletException("getTransaction error");
        }
    }

    @Override
    public String createTransaction(String coin,String address, String toAddress, BigDecimal amount,BigDecimal gas) {
        Web3j web3j = this.web3j;
        //获取币种信息
        EthToken token = EthToken.getTokenByCoin(coin);
        if (token == null) {
            throw new WalletException("not support");
        }
        int decimals = EthTokenHandle.getInstance().getTokenDecimals(web3j,token.getContract());
        //校验余额信息
        BigInteger balance = EthTokenHandle.getInstance().getTokenBalance(web3j, address, token.getContract());

        BigDecimal balanceAmout = EthConstant.getTokenAmount(balance.toString(),decimals);
        if (balanceAmout.compareTo(amount) == -1) {
            logger.error(FormatUtils.format("error balance {0},send balance {1}",balanceAmout.stripTrailingZeros().toPlainString(),amount.stripTrailingZeros().toPlainString()));
            throw new WalletException(ErrorCode.BALCNACE_ERROR);
        }

        BigInteger amountWei = EthConstant.etherTokenBigInt(amount,decimals);
        BigInteger gasInteger = null;
        if (gas != null) {
            gasInteger = EthHandle.etherToWei(gas);
        } else {
            //默认用以太坊网络的gas
            gasInteger = EthHandle.getInstance().getGasPrice(web3j);
            //矿工费增加
            gasInteger = gasInteger.multiply(BigInteger.valueOf(2));
        }
        //需要判断账户的以太坊是否足够
        BigInteger ethbalance = EthHandle.getInstance().getAddressBalance(web3j, address);
        if (ethbalance.compareTo(gasInteger) == -1) {
            throw new WalletException(ErrorCode.ETH_ENOUGH_ERROR);
        }
        return rawTransaction(address, toAddress, token.getContract(), amountWei, gasInteger,ethbalance);

    }

    @Override
    public BigDecimal queryBalance(String coin,String address) {
        Web3j web3j = this.web3j;
        //获取币种信息
        EthToken token = EthToken.getTokenByCoin(coin);
        if (token == null) {
            throw new WalletException("not support");
        }
        BigInteger balanceInteger = EthTokenHandle.getInstance().getTokenBalance(web3j, address, token.getContract());
        int decimals = EthTokenHandle.getInstance().getTokenDecimals(web3j,token.getContract());
        BigDecimal amout = EthConstant.getTokenAmount(balanceInteger.toString(),decimals);
        return amout;

    }

}
