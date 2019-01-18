package com.zhangsl.wallet.common.util;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * 通过合约地址就可以转帐token了
 *
 * Created by zhang.shaolong on 2018/3/1.
 */
public class OkHttpUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client;

    static {
        // 默认的build 连接池信息
//        ConnectionPool pool = new ConnectionPool(10, 20, TimeUnit.MINUTES);
        client=new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static Response execute(Request request) throws IOException{
        return client.newCall(request).execute();
    }

    public static void enqueue(Request request, Callback responseCallback){
        client.newCall(request).enqueue(responseCallback);
    }

    public static String  get(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = execute(request);
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String post(String url,RequestBody body) {
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = null;
        try {
            response = execute(request);
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
