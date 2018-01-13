package com.arkaces.eth_ark_channel_service.transfer;

import lombok.Data;

@Data
class EthereumEvent {

    private String id;
    private String transactionId;
    private EthereumTransaction transaction;
    private String subscriptionId;
    private String createdAt;
}
