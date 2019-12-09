package com.zhangsl.wallet.web;

import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.Result;
import com.zhangsl.wallet.common.ResultBuilder;
import com.zhangsl.wallet.common.coin.CoinType;
import com.zhangsl.wallet.common.util.JsonUtils;
import com.zhangsl.wallet.web.bean.*;
import com.zhangsl.wallet.web.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 提供对外的服务信息
 *
 * Created by zhang.shaolong on 2018/4/4.
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger("controller");

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value="/transaction/create")
    @ResponseBody
    public Result<String> create(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        logger.info("wallet transaction create parms:"+body);
        Transcation transcation = JsonUtils.toT(body, Transcation.class);
        if (transcation == null) {
            logger.error("create json to is error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if(transcation.getTo().equals(transcation.getFrom())) {
            logger.error("create from to is Equal");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if (transcation.getAmount().compareTo(BigDecimal.ZERO)< 0){
            logger.error("create amount error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if (transcation.getGas()!=null && transcation.getGas().compareTo(BigDecimal.ZERO)< 0){
            logger.error("gas amount error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        String transaction = transactionService.create(transcation);
        return ResultBuilder.buildSuccessResult(transaction);
    }

    @RequestMapping(value="/transaction/send")
    @ResponseBody
    public Result<String> send(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        logger.info("wallet transaction send parms:"+body);
        SendTranscation sendTranscation = JsonUtils.toT(body, SendTranscation.class);
        if (sendTranscation == null) {
            logger.error(" send json to is error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if (StringUtils.isEmpty(sendTranscation.getTransaction())){
            logger.error(" send transaction to is error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        String transactionId = transactionService.send(sendTranscation);
        return ResultBuilder.buildSuccessResult(transactionId);

    }

    @RequestMapping(value = "/transaction/{coinType}/decode")
    @ResponseBody
    public Result<JSONObject> decode(@PathVariable String coinType, @RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        logger.info("wallet decode send parms:"+body);
        JSONObject transactionId = transactionService.decodeHash(coinType,body);
        return ResultBuilder.buildSuccessResult(transactionId);

    }

    @RequestMapping(value="/transaction/query")
    @ResponseBody
    public Result<TransactionMessage> query(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        QueryTranscation queryTranscation = JsonUtils.toT(body, QueryTranscation.class);
        if (queryTranscation == null) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        TransactionMessage transaction = transactionService.query(queryTranscation.getCoinType(), queryTranscation.getTransaction());
        return ResultBuilder.buildSuccessResult(transaction);

    }


    @RequestMapping(value="/balance")
    @ResponseBody
    public Result<BigDecimal> queryBalance(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult();
        }
        QueryBalance queryBalance = JsonUtils.toT(body, QueryBalance.class);
        if (queryBalance == null) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        BigDecimal balance = transactionService.queryBalance(queryBalance);
        return ResultBuilder.buildSuccessResult(balance);

    }

    @RequestMapping(value="/contract/create")
    @ResponseBody
    public Result<String> createContract(@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult();
        }
        Contract contract = JsonUtils.toT(body, Contract.class);
        if (contract == null) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if (StringUtils.isEmpty(contract.getData()) || StringUtils.isEmpty(contract.getFrom())){
            logger.error(" from  or data  is error");
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        String transaction = transactionService.createContract(contract);
        return ResultBuilder.buildSuccessResult(transaction);

    }


    @RequestMapping(value = "/transaction/{coinType}/register")
    @ResponseBody
    public Result<String> registerContract(@PathVariable String coinType,@RequestBody String body) {
        if (StringUtils.isEmpty(body)) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        if (!coinType.equalsIgnoreCase(CoinType.CoinEnum.EOS.toString())) {
            return ResultBuilder.buildFailedResult(ErrorCode.PARAM_ERROR.getCode(),ErrorCode.PARAM_ERROR.getDescription());
        }
        logger.info("wallet register send parms:"+body);
        String register = transactionService.register(body);
        return ResultBuilder.buildSuccessResult(register);

    }
}
