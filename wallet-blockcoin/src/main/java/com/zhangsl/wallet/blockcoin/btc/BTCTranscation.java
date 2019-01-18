package com.zhangsl.wallet.blockcoin.btc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhangsl.wallet.common.util.JsonUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhang.shaolong on 2018/4/6.
 */
public class BTCTranscation implements Serializable {

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("blockindex")
    private long blockindex;

    @JsonProperty("txid")
    private String txid;

    @JsonProperty("block")
    private long block;

    @JsonProperty("blockhash")
    private String blockhash;

    @JsonProperty("confirmations")
    private long confirmations;

    @JsonProperty("details")
    private List<Detail> details;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getBlockindex() {
        return blockindex;
    }

    public void setBlockindex(long blockindex) {
        this.blockindex = blockindex;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public class Detail implements Serializable {

        @JsonProperty("fee")
        private BigDecimal fee; //矿工费用

        @JsonProperty("amount")
        private BigDecimal amount; //金额信息

        @JsonProperty("category")
        private String category;

        @JsonProperty("address")
        private String address;

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(BigDecimal fee) {
            this.fee = fee;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public long getBlockindex() {
            return blockindex;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public long getConfirmations() {
            return confirmations;
        }


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }


    }

    public static void main(String [] args) {
        System.out.println(JsonUtils.toJSON(new BTCTranscation()));
    }

}
