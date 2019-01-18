package com.zhangsl.wallet.blockcoin.common;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by zhang.shaolong on 2018/4/13.
 */
public class BlockProperties {

    private static Properties marketProperties = null;

    private static String PROPERTIES ="blockcoin.properties";

    private static String DEFAULT_URL = "http://127.0.0.1";


    private static int DEFAULT_CONNECT_TIME = 2000;

    private static int DEFAULT_READTIME = 2000;

    static {
        marketProperties = new Properties();
        try {
            marketProperties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRpcUser() {
        String user = marketProperties.getProperty("rpc.user");
        String password = marketProperties.getProperty("rpc.password");
        return user +":" + password;
    }


    public static List<String> getBtcAddress() {
        String address = marketProperties.getProperty("bit.address");
        return Arrays.asList(address.split(","));
    }


    public static List<String> getQtumAddress() {
        String address = marketProperties.getProperty("qtum.address");
        return Arrays.asList(address.split(","));
    }

    
    public static String getUrl(String coin) {
        String url = marketProperties.getProperty(coin.toLowerCase().toString());
        if (StringUtils.isEmpty(url)) {
            return DEFAULT_URL;
        }
        return url;
    }


    public static SimpleClientHttpRequestFactory create() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        int connectTimeout = DEFAULT_CONNECT_TIME;
        int readTimeout = DEFAULT_READTIME;
        try {
            connectTimeout = Integer.parseInt(marketProperties.getProperty("connection_timeout"));
            readTimeout = Integer.parseInt(marketProperties.getProperty("read_timeout"));
        } catch (Exception e) {
        }
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return requestFactory;
    }

}
