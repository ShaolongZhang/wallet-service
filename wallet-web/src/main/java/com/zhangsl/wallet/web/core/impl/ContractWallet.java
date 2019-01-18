package com.zhangsl.wallet.web.core.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.EthWeb3jClient;
import com.zhangsl.wallet.common.eth.EthHandle;
import com.zhangsl.wallet.common.eth.EthTokenHandle;
import com.zhangsl.wallet.common.eth.TokenFunction;
import com.zhangsl.wallet.web.bean.TransactionMessage;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/4/28.
 */
@Component
public class ContractWallet extends BaseEthWallet {

    @Override
    public TransactionMessage getTransaction(String coin, String transactionId) {
        return null;
    }

    @Override
    public String createTransaction(String coin, String address, String toAddress, BigDecimal amount, BigDecimal gas) {
        return null;
    }

    @Override
    public BigDecimal queryBalance(String coin, String address) {
        return null;
    }

    public String createContract(String from,BigInteger gasPrice, BigInteger gasLimit, String data) {
        Web3j web3j = EthWeb3jClient.getInstance().getClient();
        RawTransaction rawTransaction = EthTokenHandle.getInstance().createTokenContract(web3j, from, gasPrice,gasLimit,data);
        BigInteger ethbalance = EthHandle.getInstance().getAddressBalance(web3j, from);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from",from);
        jsonObject.put("data",rawTransaction.getData());
        BigInteger gas = validateGas(from,rawTransaction,rawTransaction.getGasLimit(),ethbalance);
        jsonObject.put("to",rawTransaction.getTo());
        jsonObject.put("value", Numeric.encodeQuantity(rawTransaction.getValue()));
        jsonObject.put("gas", Numeric.encodeQuantity(gas));
        jsonObject.put("nonce",Numeric.encodeQuantity(rawTransaction.getNonce()));
        jsonObject.put("gasPrice",Numeric.encodeQuantity(rawTransaction.getGasPrice()));
        return jsonObject.toString();
    }


    public String registerContract(String from,String key,BigInteger gasPrice, BigInteger gasLimit) {
        Web3j web3j = EthWeb3jClient.getInstance().getClient();
        Function function = TokenFunction.eosRegister(key);
        String data = FunctionEncoder.encode(function);
        BigInteger nonce = EthHandle.getInstance().getNonce(web3j, from, DefaultBlockParameterName.PENDING);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from",from);
        jsonObject.put("data",data);
        //eos执行映射的地址信息
        jsonObject.put("to","0xd0a6E6C54DbC68Db5db3A091B171A77407Ff7ccf");
        jsonObject.put("value", Numeric.encodeQuantity(BigInteger.ZERO));
        jsonObject.put("nonce",Numeric.encodeQuantity(nonce));
        jsonObject.put("gasPrice",Numeric.encodeQuantity(gasPrice));
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("from",from);
        map.put("to",jsonObject.getString("to"));
        map.put("data",jsonObject.getString("data"));
        //预计估计用到的gas
        BigInteger bigInteger  = ethCoinService.estimateGas(map);
        if (bigInteger.compareTo(gasLimit)==1) {
            //在原来的基础上加100个
            gasLimit =  bigInteger.add(BigInteger.valueOf(100L));
        }
        jsonObject.put("gas", Numeric.encodeQuantity(gasLimit));
        return jsonObject.toString();
    }
}
