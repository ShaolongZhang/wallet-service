package com.zhangsl.wallet.blockcoin.common;


import com.alibaba.fastjson.JSONObject;
import com.zhangsl.wallet.common.exception.WalletException;

/**
 * Created by d.romantsov on 22.05.2015.
 */
public class JsonRpcHandler {

    public static void checkException(JSONObject response)  {
        if (response == null)  {
            throw new WalletException("rpc error");
        }
        if (response.get("error") != null) {
            JSONObject errorJson = response.getJSONObject("error");
            String message = errorJson.getString("message");
            int code = errorJson.getInteger("code");
            throw new WalletException(message,code);
        }
    }
}
