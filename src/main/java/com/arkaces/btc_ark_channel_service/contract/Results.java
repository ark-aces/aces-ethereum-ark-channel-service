package com.arkaces.btc_ark_channel_service.contract;

import lombok.Data;

import java.util.List;

@Data
public class Results {
    private String recipientArkAddress;
    private String depositBtcAddress;
    private List<Transfer> transfers;
}