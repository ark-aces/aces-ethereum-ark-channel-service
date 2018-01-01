package com.arkaces.btc_ark_channel_service.contract;

import lombok.Data;

@Data
public class Transfer {
    private String id;
    private String createdAt;
    private String btcAmount;
    private String btcToArkRate;
    private String btcFlatFee;
    private String btcPercentFee;
    private String btcTotalFee;
    private String arkSendAmount;
    private String arkTransactionId;
}
