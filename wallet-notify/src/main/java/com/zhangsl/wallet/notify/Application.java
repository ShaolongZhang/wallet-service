package com.zhangsl.wallet.notify;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.zhangsl.wallet.common.util.FormatUtils;
import com.zhangsl.wallet.notify.listener.Listener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.math.BigInteger;


@SpringBootApplication
public class Application {
    //根据不同的参数启动notify服务
    public static void main(String[] args) {
        String type = System.getProperty("coin");
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        if (StringUtils.isEmpty(type)) {
            System.out.println(FormatUtils.format("start coin:{0} notify fail", type));
            System.exit(1);
        }
        startNotify(ctx, type);
    }

    public static void startNotify(ApplicationContext ctx, String type) {
        Listener listener = ctx.getBean(type.toUpperCase(), Listener.class);
        if (listener == null) {
            System.out.println(FormatUtils.format("start coin:{0} notify fail", type));
            System.exit(1);
        }
        //可以启动指定区块的监听服务
        String start = System.getProperty("start"); //开始启动的区块位置
        String end = System.getProperty("end"); //开始启动的区块位置
        boolean result = false;
        if (!StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)) {
            BigInteger startBlock = BigInteger.valueOf(Long.valueOf(start));
            BigInteger endBlock = BigInteger.valueOf(Long.valueOf(end));
            result = listener.start(startBlock, endBlock);
        } else {
            result = listener.start();
        }
        System.out.println(FormatUtils.format("start coin:{0} notify {1}", type, result));
    }

    @Bean
    public StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }


    @Bean
    public AmazonSQS queue() {
        AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAIFU3Z5E7XG5IQYSA", "YCZ6disqg4HdVDVbhUqMWtiKLYfwPYXHUz731axx"));
        AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();
        return builder.withRegion(Regions.AP_NORTHEAST_1).withCredentials(provider).build();
    }
}
