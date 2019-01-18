package com.zhangsl.wallet.notify.listener.coin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.blockcoin.common.BTCFactory;
import com.zhangsl.wallet.blockcoin.common.JsonRpcHandler;
import com.zhangsl.wallet.common.bean.TxBean;
import com.zhangsl.wallet.common.bean.TxType;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.redis.RedisContant;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.common.BTCListenerNotifyBean;
import com.zhangsl.wallet.notify.common.ListenerNotifyBean;
import com.zhangsl.wallet.notify.handle.WalletNotify;
import com.zhangsl.wallet.notify.listener.event.BlockEvent;
import com.zhangsl.wallet.notify.redis.TransactionRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhang.shaolong on 2018/4/8.
 */
public abstract class BaseBTCListener extends  BaseListener {

    private static final Logger logger = LoggerFactory.getLogger("listener");

    @Autowired
    private WalletNotify walletNotify;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TransactionRedis transactionRedis;

    public BaseBTCListener(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean start(){
        logger.info("start RedisSubService listener ");
        new Thread(new BaseBTCListener.RedisBlockDataThread()).start();
        new Thread(new BaseBTCListener.RedisWalletDataThread()).start();
        return true;
    }


    @Override
    public boolean start(BigInteger startBlock, BigInteger endStartBlock) {
        //TODO 默认先不处理呢
        return true;
    }


    public abstract String getWalletNotifyKey();

    public abstract String getBlockNotifyKey();

    public abstract CoinType.CoinBlockEnum getBlock();


    /**
     * 处理比特币的区块数据
     */
    private class RedisBlockDataThread implements  Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    String result = getResult(getBlockNotifyKey());
                    if (!StringUtils.isEmpty(result)) {
                        JSONObject jsonObject =  BTCFactory.getBitCoinService(getBlock().toString()).getBlock(result);
                        long height = jsonObject.getJSONObject("result").getLong("height");
                        BigInteger blockNumber = BigInteger.valueOf(height);
                        logger.info(FormatUtils.format("{0} blcock info:{1},height:{2}",getBlock().toString(),result,height));
                        //记录接收到的高度信息
                        transactionRedis.setValue(FormatUtils.format(RedisContant.BLOCK_TYPE_NUMBER_KEY,getBlock().toString()),blockNumber.toString());
                        //处理监听的收据信息
                        blockService.sendBlockMesaage(getBlock(), BlockEvent.MesageType.BLOCK, null,blockNumber);
                    }
                    Thread.sleep(200);
                } catch (Throwable e) {
                    logger.error(FormatUtils.format("{0} blcock info error",getBlock().toString()),e);
                }
            }
        }
    }

    /**
     * 处理比特币的钱包数据
     */
    private class RedisWalletDataThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String result = getResult(getWalletNotifyKey());
                    if (!StringUtils.isEmpty(result)) {
                        //TODO 根据币种可以调用不同的方法
                        txByRawTransaction(result);
                    }
                    Thread.sleep(200);
                } catch(Throwable e){
                    logger.error(FormatUtils.format("{0} wallet info error",getBlock().toString()),e);
                }
            }
        }
    }


    private String getResult(String key) {
        try {
           return stringRedisTemplate.opsForList().leftPop(key, 5, TimeUnit.SECONDS);
        } catch (Exception e){
            logger.error(FormatUtils.format("{0} getResult {1} error",getBlock().toString(),key),e);
            return null;
        }
    }

    private void txByRawTransaction(String result) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(getBlock().toString()).getRawTransaction(result);
        JsonRpcHandler.checkException(jsonObject);
        logger.info("receive transcation:"+ jsonObject.getJSONObject("result"));
        JSONObject transcation = jsonObject.getJSONObject("result");
        //交易的确认数信息
        Long confirm = transcation.getLong("confirmations");
        String txId = transcation.getString("txid");
        long blockNumer = 0L;
        if (confirm != null && confirm.longValue() > 0) {
            //获取当时的高度信息
            String blockhash = transcation.getString("blockhash");
            JSONObject jsonObjectBlock = BTCFactory.getBitCoinService(getBlock().toString()).getBlock(blockhash);
            blockNumer = jsonObjectBlock.getJSONObject("result").getLong("height");
        }
        voutRaw(transcation,txId,blockNumer,confirm);
    }


    private void voutRaw(JSONObject transcation,String txId,long blockNumer,long confirm) {
        //获取具体的信息
        JSONArray voutArray = transcation.getJSONArray("vout");
        if(voutArray!= null && voutArray.size()>0) {
            //判断是否是我们交易所发出去的
            voutDetails(voutArray, txId, blockNumer, confirm);
            if (blockNumer >0) {
                //删除标记信息
                transactionRedis.deleteTxid(getBlock().toString(),txId);
            }
        }
    }


    /**
     * 解析vount,vount的数据肯定都是接收数据信息
     *
     * @param detailArray
     * @param txId
     * @param blockNumer
     */
    private void voutDetails(JSONArray detailArray,String txId,long blockNumer,long confirm) {
        List<JSONObject> vounts = detailArray.toJavaList(JSONObject.class);
        if (vounts != null) {
            //遍历out结果信息
            vounts.forEach(object -> {
                BigDecimal amount = object.getBigDecimal("value");
                JSONArray addresses = object.getJSONObject("scriptPubKey").getJSONArray("addresses");
                String address = addresses.getString(0);
                if (transactionRedis.isMyAddress(getBlock().toString(),address)) {
                    logger.info(FormatUtils.format("coin:{0},address:{1},tx:{2}", getBlock().toString(), address, txId));
                    //确认数信息
                    send(address,amount, null, TxType.RECEIVE.toString(),txId, blockNumer, confirm);
                }
            });
            long hasTxid = transactionRedis.hasTxid(getBlock().toString(),txId);
            //我们交易所发出去的
            if (hasTxid >= 0) {
                decRawSend(vounts.get(0).getBigDecimal("value"), txId, blockNumer, confirm);
            }
        }
    }


    private void decRawSend(BigDecimal amount,String txId, long blockNumer, long confirm) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(getBlock().toString()).getTransaction(txId);
        JsonRpcHandler.checkException(jsonObject);
        JSONObject transcation = jsonObject.getJSONObject("result");
        //确认数信息
        BigDecimal gasPrice = transcation.getBigDecimal("fee");
        send("",amount, gasPrice,TxType.SEND.toString(),txId, blockNumer, confirm);
    }


    private void send(String address,BigDecimal amount,BigDecimal gasPrice,String type,String txId,long blockNumer,long confirm){
        TxBean txBean = walletNotify.buildNotify(address, getBlock().toString(), amount, gasPrice, type, txId, BigInteger.valueOf(blockNumer));
        if (blockNumer>0) {
            if (StringUtils.isEmpty(address)) {
                transactionRedis.addTransactoinByType(txBean);
            } else {
                transactionRedis.addTransactoinByAddress(txBean);
            }
            //处理钱包的的收据信息
            BTCListenerNotifyBean notifyBean = new BTCListenerNotifyBean(type,txId,address);
            blockService.sendBlockMesaage(getBlock(), BlockEvent.MesageType.TRANSCATION, notifyBean, BigInteger.valueOf(blockNumer));
        }
        //第一次收到通知进行通知，比特币就通知1-2两次
        walletNotify.notify(txBean, confirm);
    }

    /**
     * 解析tx的流程信息,以下都是tx的流程方法
     *
     *
     * @param result
     */
    private void tx(String result) {
        JSONObject jsonObject = BTCFactory.getBitCoinService(getBlock().toString()).getTransaction(result);
        JsonRpcHandler.checkException(jsonObject);
        logger.info("receive transcation:"+ jsonObject.getJSONObject("result"));
        JSONObject transcation = jsonObject.getJSONObject("result");
        //交易的确认数信息
        long confirm = transcation.getLong("confirmations");
        String txId = transcation.getString("txid");
        long blockNumer = 0L;
        if(confirm ==0 ) {
           //只需要把tx存储起来
            logger.info("receive transcation:"+ txId +" confirm is 0");
            //TODO 业务逻辑处理
        } else {
            //获取当时的高度信息
            String blockhash = transcation.getString("blockhash");
            JSONObject jsonObjectBlock =  BTCFactory.getBitCoinService(getBlock().toString()).getBlock(blockhash);
            blockNumer = jsonObjectBlock.getJSONObject("result").getLong("height");
        }
        detail(transcation,txId,blockNumer,confirm);
    }

    private void detail(JSONObject transcation,String txId,long blockNumer,long confirm) {
        //获取具体的信息
        JSONArray detailArray = transcation.getJSONArray("details");
        if(detailArray!= null && detailArray.size()>0) {
            BigDecimal amount = transcation.getBigDecimal("amount");
            long del = transactionRedis.hasTxid(getBlock().toString(),txId);
            if (amount.compareTo(BigDecimal.ZERO)==-1) {
                decSend(transcation,txId,blockNumer,confirm);
            } else {
                decDetails(detailArray,txId,blockNumer,confirm);
                //我们交易所发出去的
                if (del>0) {
                    decSend(transcation,txId,blockNumer,confirm);
                }
            }

        }
    }



    private void decSend(JSONObject transcation, String txId, long blockNumer, long confirm) {
        //确认数信息
        BigDecimal amount = transcation.getBigDecimal("amount");
        BigDecimal gasPrice = transcation.getBigDecimal("fee");
        TxBean txBean = walletNotify.buildNotify("", getBlock().toString(), amount, gasPrice, TxType.SEND.toString(), txId, BigInteger.valueOf(blockNumer));
        if (blockNumer>0) {
            transactionRedis.addTransactoinByType(txBean);
            //处理钱包的的收据信息
            ListenerNotifyBean notifyBean = new ListenerNotifyBean(TxType.SEND.toString(),txId);
            blockService.sendBlockMesaage(getBlock(), BlockEvent.MesageType.TRANSCATION, notifyBean, BigInteger.valueOf(blockNumer));
        }
        //第一次收到通知进行通知，比特币就通知1-2两次
        walletNotify.notify(txBean, confirm);

    }


    /**
     * 解析details
     *
     * @param detailArray
     * @param txId
     * @param blockNumer
     */
    private void decDetails(JSONArray detailArray,String txId,long blockNumer,long confirm) {
        List<JSONObject> details = detailArray.toJavaList(JSONObject.class);
        if (details != null) {
            details.forEach(object -> {
                String address = object.getString("address");
                if (transactionRedis.isMyAddress(getBlock().toString(),address)) {
                    logger.info(FormatUtils.format("coin:{0},address:{1},tx:{2}", getBlock().toString(), address, txId));
                    //receive,send,move,conflicted
                    String category = object.getString("category");
                    //确认数信息
                    BigDecimal amount = object.getBigDecimal("amount");
                    BigDecimal gasPrice = object.getBigDecimal("fee");
                    TxBean txBean = walletNotify.buildNotify(address, getBlock().toString(), amount, gasPrice, category, txId,BigInteger.valueOf(blockNumer));
                    if (blockNumer >0) {
                        transactionRedis.addTransactoinByType(txBean);
                        //处理钱包的的收据信息
                        ListenerNotifyBean notifyBean = new ListenerNotifyBean(category,txId);
                        blockService.sendBlockMesaage(getBlock(), BlockEvent.MesageType.TRANSCATION, notifyBean,BigInteger.valueOf(blockNumer));
                    }
                    //第一次收到通知进行通知，比特币就通知1-2两次
                    walletNotify.notify(txBean,confirm);
                }
            });
        }
    }


}
