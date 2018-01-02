package com.arkaces.btc_ark_channel_service.transfer;

import com.arkaces.btc_ark_channel_service.contract.ContractEntity;
import com.arkaces.btc_ark_channel_service.contract.ContractRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BitcoinEventHandler {

    private final ContractRepository contractRepository;
    private final TransferRepository transferRepository;
    
    @PostMapping("/bitcoinEvents")
    public ResponseEntity<Void> handleBitcoinEvent(@RequestBody JsonNode event) {
        // todo: verify event is signed by listener
        String btcTransactionId = event.get("transactionId").toString();
        
        log.info("Received Bitcoin event: " + btcTransactionId + " -> " + event.get("data"));
        
        String subscriptionId = event.get("subscriptionId").asText();
        ContractEntity contractEntity = contractRepository.findOneBySubscriptionId(subscriptionId);
        if (contractEntity != null) {
            log.info("Matched event for contract id " + contractEntity.getId());
            
            // todo: create transfer entity and save to contract      
            // todo: we should probably just save these and process them async
            TransferEntity transferEntity = new TransferEntity();
            transferEntity.setBtcTransactionId(btcTransactionId);
            
            transferRepository.save(transferEntity);
            
            log.info("Saved transfer id " + transferEntity.getId() + " to contract " + contractEntity.getId());
        }
        
        return ResponseEntity.ok().build();
    }
}
