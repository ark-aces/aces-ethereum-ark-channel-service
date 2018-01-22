package com.arkaces.eth_ark_channel_service.transfer;

import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
public class TransferMapper {

    public Transfer map(TransferEntity transferEntity) {
        Transfer transfer = new Transfer();
        transfer.setId(transferEntity.getId());
        transfer.setStatus(transferEntity.getStatus());
        transfer.setCreatedAt(transferEntity.getCreatedAt().atOffset(ZoneOffset.UTC).toString());
        transfer.setEthTransactionId(transferEntity.getEthTransactionId());
        transfer.setEthAmount(transferEntity.getEthAmount().toPlainString());
        transfer.setEthToArkRate(transferEntity.getEthToArkRate().toPlainString());
        transfer.setEthFlatFee(transferEntity.getEthFlatFee().toPlainString());
        transfer.setEthPercentFee(transferEntity.getEthPercentFee().toPlainString());
        transfer.setEthTotalFee(transferEntity.getEthTotalFee().toPlainString());
        transfer.setArkTransactionId(transferEntity.getArkTransactionId());
        transfer.setArkSendAmount(transferEntity.getArkSendAmount().toPlainString());
        return transfer;
    }
}
