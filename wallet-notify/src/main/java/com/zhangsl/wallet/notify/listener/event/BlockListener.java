package com.zhangsl.wallet.notify.listener.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 处理区块和交易的信息
 *
 * Created by zhang.shaolong on 2018/3/10.
 */
@Component
public class  BlockListener extends ApplicationObjectSupport implements ApplicationListener<BlockEvent>  {

    @Override
    public void onApplicationEvent(BlockEvent event) {
        if (event == null) {
            return ;
        }
        if (event.getNumberBlock() == null) {
            return;
        }
        if (event.getCoinEnum() == null) {
            return;
        }
        //开始处理信息
        BlockHandle blockHandle = getBlockHandle(event.getCoinEnum().getBlock());
        if (blockHandle != null) {
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("numberBlock",event.getNumberBlock().intValue());
            params.put("coin",event.getCoinEnum().toString());
            switch (event.getMesageType()) {
                case BLOCK:
                    blockHandle.handleBlock(event.getNumberBlock(),params);
                    break;
                case TRANSCATION:
                    blockHandle.handle(event.getMessage(),params);
                    break;
                default:
                    break;
            }

        }
    }

    public BlockHandle getBlockHandle(String currencyName) {
        ApplicationContext ctx = getApplicationContext();
        return ctx.getBean(currencyName, BlockHandle.class);
    }
}
