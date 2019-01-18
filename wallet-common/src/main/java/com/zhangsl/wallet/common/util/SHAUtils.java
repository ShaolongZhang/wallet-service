package com.zhangsl.wallet.common.util;


import com.zhangsl.wallet.common.ErrorCode;
import com.zhangsl.wallet.common.exception.WalletException;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * Created by zhang.shaolong on 2018/5/3.
 */
public class SHAUtils {

    public static final String ENCODE= "UTF-8";


    public static String getSHA256(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            String baseStr = Base64.getEncoder().encodeToString(str.getBytes(ENCODE));
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(baseStr.getBytes(ENCODE));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            throw new WalletException(ErrorCode.SIGN_ERROR);
        }
        return encodeStr;
    }



    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
