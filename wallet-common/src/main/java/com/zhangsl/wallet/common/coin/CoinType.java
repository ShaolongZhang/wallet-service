package com.zhangsl.wallet.common.coin;

/**
 * 币信息配置
 *
 * Created by zhang.shaolong on 2018/3/12.
 */
public class CoinType {

    public static String getCoinWallet(String coinType) {
        for(CoinEnum coin : CoinEnum.values()) {
            if (coin.toString().equalsIgnoreCase(coinType)) {
                return coin.getWallet();
            }
        }
        return null;
    }

    public static String getCoinBlock(String coinType) {
        for(CoinBlockEnum coin : CoinBlockEnum.values()) {
            if (coin.toString().equalsIgnoreCase(coinType)) {
                return coin.block;
            }
        }
        return null;
    }

    public static CoinBlockEnum getCoinBlockEnum(String coinType) {
        for(CoinBlockEnum coin : CoinBlockEnum.values()) {
            if (coin.toString().equalsIgnoreCase(coinType)) {
                return coin;
            }
        }
        return null;
    }

    public static CoinEnum getCoinEnum(String coinType) {
        for(CoinEnum coin : CoinEnum.values()) {
            if (coin.toString().equalsIgnoreCase(coinType)) {
                return coin;
            }
        }
        return null;
    }

    public static enum CoinEnum {
        //wallet 代表的是币种的钱包服务名称
        BTC("BTC"),
        BCH("BTC"),
        LTC("BTC"),
        ETH("ETH"),
        QTUM("BTC"),
        TB("ERC20TOKEN"),
        USDT("ERC20TOKEN"),
        EOS("ERC20TOKEN");

        private String wallet;

        CoinEnum(String wallet) {
            this.wallet = wallet;
        }

        public String getWallet() {
            return wallet;
        }
    }


    public static enum CoinBlockEnum {
        //block 代表的是币种的区块监听服务
        BTC("BTCBLOCK"),
        BCH("BCHBLOCK"),
        LTC("LTCBLOCK"),
        ETH("ETHBLOCK"),
        TB("TBBLOCK"),
        QTUM("QTUMBLOCK"),
        USDT("USDTBLOCK"),
        EOS("EOSBLOCK");

        private String block;

        CoinBlockEnum(String block) {
            this.block = block;
        }

        public String getBlock() {
            return block;
        }
    }
}
