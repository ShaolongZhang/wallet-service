package com.zhangsl.wallet.web.core;


import com.zhangsl.wallet.web.bean.TransactionMessage;

import java.math.BigDecimal;

public interface Wallet {

  /**
   * 查询交易信息
   *
   * @param transactionId
   * @return
   */
  TransactionMessage getTransaction(String coin, String transactionId);

  /**
   * 创建交易信息
   * @param address
   * @param amount
   * @return
   */
  String createTransaction(String coin, String address, String toAddress, BigDecimal amount, BigDecimal gas);

  /**
   * 发送信息，已经是加密好的信息进行发送
   *
   * @param transactionString
   * @return
   */
  String sendSignedTransaction(String coin, String transactionString);

  /**
   * 区块查询账户的余额信息
   *
   * @return
   */
  BigDecimal queryBalance(String coin, String address);
}
