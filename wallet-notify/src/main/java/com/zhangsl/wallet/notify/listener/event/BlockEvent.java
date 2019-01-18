package com.zhangsl.wallet.notify.listener.event;

import com.zhangsl.wallet.common.coin.CoinType;
import org.springframework.context.ApplicationEvent;

import java.math.BigInteger;

/**
 *
 *  区块的信息
 * Created by zhang.shaolong on 2018/3/10.
 */
public class BlockEvent<T> extends ApplicationEvent {

    private CoinType.CoinBlockEnum coinEnum;

    private MesageType mesageType; //消息类型

    private T message;

    private BigInteger numberBlock;

    public BlockEvent(Object source, CoinType.CoinBlockEnum  coinEnum, MesageType mesageType,T message) {
        this(source,coinEnum,mesageType,message,null);
    }

    public BlockEvent(Object source, CoinType.CoinBlockEnum coinEnum, MesageType mesageType,BigInteger numberBlock) {
        this(source,coinEnum,mesageType,null,numberBlock);
    }

    public BlockEvent(Object source, CoinType.CoinBlockEnum coinEnum, MesageType mesageType, T message,BigInteger numberBlock) {
        super(source);
        this.coinEnum = coinEnum;
        this.numberBlock = numberBlock;
        this.message = message;
        this.mesageType = mesageType;
    }

    public BigInteger getNumberBlock() {
        return numberBlock;
    }

    public void setNumberBlock(BigInteger numberBlock) {
        this.numberBlock = numberBlock;
    }

    public CoinType.CoinBlockEnum getCoinEnum() {
        return coinEnum;
    }

    public void setCoinEnum(CoinType.CoinBlockEnum coinEnum) {
        this.coinEnum = coinEnum;
    }


    public T getMessage() {
        return message;
    }

    public MesageType getMesageType() {
        return mesageType;
    }

    public void setMesageType(MesageType mesageType) {
        this.mesageType = mesageType;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public static enum MesageType {
        BLOCK,
        TRANSCATION;
    }
}
