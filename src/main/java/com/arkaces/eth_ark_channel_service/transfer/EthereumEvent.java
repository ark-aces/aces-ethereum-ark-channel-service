package com.arkaces.eth_ark_channel_service.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class EthereumEvent {

    private String id;
    private String transactionId;

    @JsonProperty("data")
    private EthereumTransaction transaction;

    private String subscriptionId;
    private String createdAt;
}
