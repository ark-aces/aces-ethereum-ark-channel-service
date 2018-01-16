package com.arkaces.eth_ark_channel_service.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransferStatus {

    NEW("new"),
    COMPLETE("complete"),
    FAILED("failed");

    @Getter
    private String status;
}
