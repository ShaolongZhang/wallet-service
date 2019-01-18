package com.zhangsl.wallet.common.util;

import java.util.regex.Matcher;

/**
 * Created by zhang.shaolong
 */
public class FormatUtils {

    public static String format(String message, Object... params) {
        if (message == null)  {
            return null;
        }
        if (params == null || params.length == 0) {
            return message;
        }
        for (int i = 0; i < params.length; i++) {
            message = message.replaceAll("\\{"+ i +"\\}", params[i] == null ? "null" : Matcher.quoteReplacement(String.valueOf(params[i].toString())));
        }
        return message;
    }
}
