package com.arkaces.eth_ark_channel_service.contract;

import com.arkaces.eth_ark_channel_service.transfer.Transfer;
import lombok.Data;

import java.util.List;

@Data
public class Results {
    private String recipientArkAddress;
    private String depositBtcAddress;
    private List<Transfer> transfers;
}