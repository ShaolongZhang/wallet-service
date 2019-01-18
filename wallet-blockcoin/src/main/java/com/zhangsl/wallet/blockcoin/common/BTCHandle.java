package com.zhangsl.wallet.blockcoin.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.btc.BitCoinService;
import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.btc.BTCTranscation;
import com.zhangsl.wallet.common.btc.TxInput;
import com.zhangsl.wallet.common.btc.TxOutput;
import com.zhangsl.wallet.common.btc.Unspent;
import com.zhangsl.wallet.common.exception.WalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by zhang.shaolong on 2018/4/7.
 */
public class BTCHandle {

    private static final Logger logger = LoggerFactory.getLogger(BTCHandle.class);


    /**  默认的矿工费  */
    private static final BigDecimal GAS= BigDecimal.valueOf(0.0001d);


    private static class BTCHandleHolder{
        private static BTCHandle btcHandle = new BTCHandle();
    }

    private BTCHandle(){}

    public static BTCHandle getInstance() {
        return BTCHandle.BTCHandleHolder.btcHandle;
    }


    /**
     * 获取交易确认数信息
     *
     * @param txHash
     * @return
     */
    public long getConfirm(BitCoinService bitCoinService, String txHash ) {
        JSONObject jsonObject = bitCoinService.getTransaction(txHash);
        JsonRpcHandler.checkException(jsonObject);
        long confirm = jsonObject.getJSONObject("result").getLong("confirmations");
        return confirm;
    }


    public String createQtumTransaction(BitCoinService bitCoinService,String fromAddress,String toAddress, BigDecimal amount) {
        validateBalance(bitCoinService,amount);
        List<TxInput> inputs = new LinkedList<TxInput>();
        List<BTCTranscation.SignTxInput> signinputs = new LinkedList<BTCTranscation.SignTxInput>();
        //地址信息
        List<String> adds = BlockProperties.getQtumAddress();
        List<Unspent> unspentList = getUnspentList(bitCoinService,adds);
        BigDecimal t = new BigDecimal(0L);
        //找零地址信息
        for (Unspent us : unspentList) {
            //构造信息
            TxInput input = new BTCTranscation.BasicTxInput(us.getTxid(), us.getVout());
            BTCTranscation.SignTxInput signTxInput = new BTCTranscation.SignTxInput(us.getTxid(), us.getVout(),us.getScriptPubKey());
            inputs.add(input);
            signinputs.add(signTxInput);
            t = t.add(BigDecimal.valueOf(us.getAmount()));
            if (t.compareTo(amount)==1 ) {
                break;
            }
        }
        //将找零地址转到fromAddress 地址信息
        List<TxOutput> txOutputs = createTxOutput(toAddress,amount);
        String hash = createRawTransaction(bitCoinService,inputs,txOutputs);
        return createResult(hash,signinputs);

    }

    /**
     * 矿工费需要计算
     * @param toAddress
     * @param amount
     * @return
     */
    public String createRawTransaction(BitCoinService bitCoinService,String fromAddress,String toAddress, BigDecimal amount,BigDecimal gas) {
        validateBalance(bitCoinService,amount);
        List<TxInput> inputs = new LinkedList<TxInput>();
        List<BTCTranscation.SignTxInput> signinputs = new LinkedList<BTCTranscation.SignTxInput>();
        if (gas == null) {
            try {
                //每千字节的手续费
                gas = getFeerate(bitCoinService, 6);
            } catch (Exception e) {
                gas = GAS;
            }
        }
        BigDecimal v = amount.add(gas);
        List<Unspent> unspentList = getUnspentList(bitCoinService,v,v);
        BigDecimal t = new BigDecimal(0L);
        //找零地址信息
        for (Unspent us : unspentList) {
            //构造信息
            TxInput input = new BTCTranscation.BasicTxInput(us.getTxid(), us.getVout());
            BTCTranscation.SignTxInput signTxInput = new BTCTranscation.SignTxInput(us.getTxid(), us.getVout(),us.getScriptPubKey(),us.getAmount());
            inputs.add(input);
            signinputs.add(signTxInput);
            t = t.add(BigDecimal.valueOf(us.getAmount()));
            if (t.compareTo(v)==1 ) {
                break;
            }
        }
        BigDecimal amountChange = t.subtract(v);
        //将找零地址转到fromAddress 地址信息
        List<TxOutput> txOutputs = createTxOutput(toAddress,amount,fromAddress,amountChange);
        String hash = createRawTransaction(bitCoinService,inputs,txOutputs);
        return createResult(hash,signinputs);
    }


    private void validateBalance(BitCoinService bitCoinService,BigDecimal amount) {
        JSONObject balanceObject = bitCoinService.getBalance();
        JsonRpcHandler.checkException(balanceObject);
        BigDecimal balance = balanceObject.getBigDecimal("result");
        //钱包整体的余额不够
        if (balance.compareTo(amount) == -1) {
            throw new WalletException(ErrorCode.BALCNACE_ERROR);
        }
    }


    private String createResult(String hash,List<BTCTranscation.SignTxInput> signinputs) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hash",hash);
        jsonObject.put("out",signinputs);
        return jsonObject.toJSONString();
    }


    /**
     *
     * @param bitCoinService
     * @return
     */
    private List<Unspent> getUnspentList(BitCoinService bitCoinService,List<String> address) {
        List<Unspent> unspentList = null;
        JSONObject objectUnspent = bitCoinService.listUnspent(6,999999, address);
        JsonRpcHandler.checkException(objectUnspent);
        JSONArray jsonArray = objectUnspent.getJSONArray("result");
        //有直接转帐
        unspentList = jsonArray.toJavaList(Unspent.class);
        return unspentList;

    }


    /**
     * 递归寻找
     *
     * @param bitCoinService
     * @param minAmount
     * @param sumMinAmount
     * @return
     */
    private List<Unspent> getUnspentList(BitCoinService bitCoinService,BigDecimal minAmount,BigDecimal sumMinAmount) {
        List<Unspent> unspentList = null;
        Map<String,Object> options = new HashMap<String,Object>();
        options.put("minimumAmount",minAmount);
        options.put("minimumSumAmount",sumMinAmount);
        JSONObject objectUnspent = bitCoinService.listUnspent(6,999999, new ArrayList<String>(),options);
        JsonRpcHandler.checkException(objectUnspent);
        JSONArray jsonArray = objectUnspent.getJSONArray("result");
        if (jsonArray.size()==0) {
            //没有直接大于这个的信息，从新构造
            BigDecimal minTemp = minAmount.divide(BigDecimal.valueOf(2),2, BigDecimal.ROUND_HALF_UP);
            return getUnspentList(bitCoinService,minTemp,sumMinAmount);
        } else {
            //有直接转帐
            unspentList = jsonArray.toJavaList(Unspent.class);
            return unspentList;
        }
    }


    /**
     * @param toAddress
     * @param amount
     * @return
     */
    public String fundrawtransaction(BitCoinService bitCoinService,String from,String toAddress, BigDecimal amount) {
        List inputs = new LinkedList<TxInput>();
        List<TxOutput> txOutputs = createTxOutput(toAddress,amount);
        String hash = createRawTransaction(bitCoinService,inputs,txOutputs);
        Map<String, String> outMaps= new HashMap<String,String>();
        outMaps.put("changeAddress",from);
        JSONObject json = bitCoinService.fundRawTransaction(hash,outMaps);
        JsonRpcHandler.checkException(json);
        return json.getJSONObject("result").getString("hex");
    }



    public String createRawTransaction(BitCoinService bitCoinService,List<TxInput> inputs, List<TxOutput> outputs)  {
        Map<String, Double> outMaps= new LinkedHashMap();
        outputs.forEach(txOutput -> {
            outMaps.put(txOutput.address(), txOutput.amount());
        });
        JSONObject json = bitCoinService.createRawTransaction(inputs.toArray(),outMaps);
        JsonRpcHandler.checkException(json);
        return json.getString("result");
    }


    /**
     *
     * @param recipientAddress
     * @param amountSend
     * @return
     */
    public List<TxOutput> createTxOutput(String recipientAddress,BigDecimal amountSend) {
        List<TxOutput> outputs = new ArrayList<TxOutput>();
        outputs.add(new BTCTranscation.BasicTxOutput(recipientAddress, amountSend.doubleValue()));
        return outputs;
    }

    /**
     * 需要设置找零地址信息
     * @param recipientAddress
     * @param amountSend
     * @param sendeAddress
     * @param amountChange
     * @return
     */
    public List<TxOutput> createTxOutput(String recipientAddress,BigDecimal amountSend,String sendeAddress,BigDecimal amountChange) {
        List<TxOutput> outputs = new ArrayList<TxOutput>();
        outputs.add(new BTCTranscation.BasicTxOutput(recipientAddress, getDouble(amountSend)));
        outputs.add(new BTCTranscation.BasicTxOutput(sendeAddress, getDouble(amountChange)));
        return outputs;
    }


    public BigDecimal getFeerate(BitCoinService bitCoinService,int confirm) {
        JSONObject json = bitCoinService.estimatesmartfee(confirm);
        return json.getJSONObject("result").getBigDecimal("feerate");
    }

    public double getDouble(BigDecimal value) {
        return Double.valueOf(value.stripTrailingZeros().toPlainString()).doubleValue();
    }
}
