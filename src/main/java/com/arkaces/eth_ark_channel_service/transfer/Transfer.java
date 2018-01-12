package com.arkaces.eth_ark_channel_service.transfer;

import lombok.Data;

@Data
public class Transfer {

    private String id;
    private String status;
    private String createdAt;
    private String ethTransactionId;
    private String ethAmount;
    private String ethToArkRate;
    private String ethFlatFee;
    private String ethPercentFee;
    private String ethTotalFee;
    private String arkSendAmount;
    private String arkTransactionId;
}
