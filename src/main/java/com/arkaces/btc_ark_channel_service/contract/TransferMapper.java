package com.arkaces.btc_ark_channel_service.contract;

import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
public class TransferMapper {
    
    public Transfer map(TransferEntity transferEntity) {
        Transfer transfer = new Transfer();
        transfer.setId(transferEntity.getId());
        transfer.setArkSendAmount(transferEntity.getArkSendAmount().toPlainString());
        transfer.setArkTransactionId(transferEntity.getArkTransactionId());
        transfer.setBtcAmount(transferEntity.getBtcAmount().toPlainString());
        transfer.setBtcFlatFee(transferEntity.getBtcFlatFee().toPlainString());
        transfer.setBtcPercentFee(transferEntity.getBtcPercentFee().toPlainString());
        transfer.setBtcToArkRate(transferEntity.getBtcToArkRate().toPlainString());
        transfer.setBtcTotalFee(transferEntity.getBtcTotalFee().toPlainString());
        transfer.setCreatedAt(transferEntity.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        
        return transfer;
    }
    
}
