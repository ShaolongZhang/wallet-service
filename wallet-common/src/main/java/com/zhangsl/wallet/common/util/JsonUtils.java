package com.zhangsl.wallet.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtils {

    private static ThreadLocal<ObjectMapper> objMapperLocal = new ThreadLocal<ObjectMapper>(){
        @Override
        public ObjectMapper initialValue(){
            return new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);
        }
    };

    public static String toJSON(Object value) {
        String result = null;
        try {
            result = objMapperLocal.get().writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("null".equals(result)) {
            result = null;
        }
        return result;
    }

    public static <T> T toT(String jsonString, Class<T> clazz) {
        try {
            return objMapperLocal.get().readValue(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

