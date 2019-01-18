package com.zhangsl.wallet.common.btc;

/**
 * Created by zhang.shaolong on 2018/4/7.
 */
public class BTCTranscation {

    public static class BasicTxInput implements TxInput {

        public String txid;
        public int vout;

        public BasicTxInput(String txid, int vout) {
            this.txid = txid;
            this.vout = vout;
        }

        @Override
        public String txid() {
            return txid;
        }

        @Override
        public int vout() {
            return vout;
        }

    }


    public static class SignTxInput implements TxInput {

        public String txid;
        public int vout;
        public String scriptPubKey;
        public double amount;

        public SignTxInput(String txid, int vout,String scriptPubKey) {
            this.txid = txid;
            this.vout = vout;
            this.scriptPubKey=scriptPubKey;
        }

        public SignTxInput(String txid, int vout,String scriptPubKey,double amount) {
            this.txid = txid;
            this.vout = vout;
            this.scriptPubKey=scriptPubKey;
            this.amount= amount;
        }

        @Override
        public String txid() {
            return txid;
        }

        @Override
        public int vout() {
            return vout;
        }

        public String scriptPubKey() {
            return scriptPubKey;
        }
        public double amount() {
            return amount;
        }
    }

    public static class BasicTxOutput implements TxOutput {

        public String address;
        public double amount;

        public BasicTxOutput(String address, double amount) {
            this.address = address;
            this.amount = amount;
        }

        @Override
        public String address() {
            return address;
        }

        @Override
        public double amount() {
            return amount;
        }
    }

}
