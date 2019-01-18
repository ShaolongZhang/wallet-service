package com.zhangsl.wallet.common.redis;


/**
 * Created by zhang.shaolong
 */
public class RedisContant {

    /**
     * 混存简单的交易信息 ex:T_ETH_324324324343 {"from","to",:value"}
     */
    public static final String TYPE_TRANSCATION = "T_{0}_{1}";


    //币种交易详情 T_ETH set集合
    public static final String TRANSCATION_TYPE = "T_{0}";

    public static final String TRANSCATION_FROM = "T_F_{0}";


    /**
     * 混存简单的交易信息 ex:T_ETH_RECEIVE_324324324343 {"from","to",:value"}
     */
    public static final String TYPE_TRANSCATION_TYPE = "T_{0}_{1}_{2}";

    public static final String TYPE_TRANSCATION_ADDESS = "A_{0}_{1}_{2}";


    /**
     * ex: ADDRESS_TB_xxxxxxxxx
     */
    public static final String ADDRESS_TYPE="ADDRESS_{0}_{1}";

    //存储业务区块的高度信息 ex: BLOCK_ETH_NUMBER
    public static String BLOCK_TYPE_NUMBER_KEY ="BLOCK_{0}_NUMBER";

    public static String BLOCK_TYPE_TRNSACTION_NUMBER_KEY ="BLOCK_T_{0}_NUMBER";

    public static String NOTIFY_C_T ="NOTIFY_{0}_{1}_{2}";


    /**
     * 地址的anoce信息
     */
    public static String A_N ="ADDRESS_NONCE_{0}";


    public static final String SENT_TYPE_TRANSCATION = "S_{0}_T";

    public static final String TRANSCATION_SIGN = "T_S_{0}";


}
