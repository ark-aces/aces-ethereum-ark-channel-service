package com.arkaces.eth_ark_channel_service.transfer;

import ark_java_client.ArkClient;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import com.arkaces.eth_ark_channel_service.FeeSettings;
import com.arkaces.eth_ark_channel_service.ServiceArkAccountSettings;
import com.arkaces.eth_ark_channel_service.ark.ArkSatoshiService;
import com.arkaces.eth_ark_channel_service.contract.ContractEntity;
import com.arkaces.eth_ark_channel_service.contract.ContractRepository;
import com.arkaces.eth_ark_channel_service.exchange_rate.ExchangeRateService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EthereumEventHandler {

    private final ContractRepository contractRepository;
    private final TransferRepository transferRepository;
    private final IdentifierGenerator identifierGenerator;
    private final ExchangeRateService exchangeRateService;
    private final ArkClient arkClient;
    private final ArkSatoshiService arkSatoshiService;
    private final ServiceArkAccountSettings serviceArkAccountSettings;
    private final FeeSettings feeSettings;

    @PostMapping("/ethereumEvents")
    public ResponseEntity<Void> handleEthereumEvent(@RequestBody JsonNode event) {
        // todo: verify event post is signed by listener
        String ethTransactionId = event.get("transactionId").toString();
        
        log.info("Received Ethereum event: " + ethTransactionId + " -> " + event.get("data"));
        
        String subscriptionId = event.get("subscriptionId").asText();
        ContractEntity contractEntity = contractRepository.findOneBySubscriptionId(subscriptionId);
        if (contractEntity != null) {
            // todo: lock contract for update to prevent concurrent processing of a listener transaction.
            // Listeners send events serially, so that shouldn't be an issue, but we might want to lock
            // to be safe.

            log.info("Matched event for contract id " + contractEntity.getId() + " eth transaction id " + ethTransactionId);

            String transferId = identifierGenerator.generate();

            TransferEntity transferEntity = new TransferEntity();
            transferEntity.setId(transferId);
            transferEntity.setCreatedAt(LocalDateTime.now());
            transferEntity.setEthTransactionId(ethTransactionId);
            transferEntity.setContractEntity(contractEntity);

            // Get ETH amount from transaction
            // JsonNode transaction = ethereumService.getTransaction(ethTransactionId);
            // BigDecimal ethAmount = transaction.get("amount").decimalValue();
            // BigDecimal ethFee = transaction.get("fee").decimalValue(); // todo: is this fee included in amount?
            // transferEntity.setEthAmount(ethAmount);

            BigDecimal ethToArkRate = exchangeRateService.getRate("ETH", "ARK"); //2027.58, Ark 8, Eth 15000
            transferEntity.setEthToArkRate(ethToArkRate);

            // Set fees
            transferEntity.setEthFlatFee(feeSettings.getEthFlatFee());
            transferEntity.setEthPercentFee(feeSettings.getEthPercentFee());

            // BigDecimal percentFee = feeSettings.getEthPercentFee()
            //         .divide(new BigDecimal("100.00"), 8, BigDecimal.ROUND_HALF_UP);
            // BigDecimal ethTotalFeeAmount = ethAmount.multiply(percentFee).add(feeSettings.getEthFlatFee());
            // transferEntity.setEthTotalFee(ethTotalFeeAmount);

            // Calculate send ark amount
            BigDecimal arkSendAmount = BigDecimal.ZERO;
            // if (ethAmount.compareTo(ethTotalFeeAmount) > 0) {
            //     arkSendAmount = ethAmount.multiply(ethToArkRate).setScale(8, RoundingMode.HALF_DOWN);
            // }
            // transferEntity.setArkSendAmount(arkSendAmount);

            transferEntity.setStatus(TransferStatus.NEW);
            transferRepository.save(transferEntity);

            // Send ark transaction
            Long arkSendSatoshis = arkSatoshiService.toSatoshi(arkSendAmount);
            String arkTransactionId = arkClient.broadcastTransaction(
                contractEntity.getRecipientArkAddress(),
                arkSendSatoshis,
                null,
                serviceArkAccountSettings.getPassphrase()
            );
            transferEntity.setArkTransactionId(arkTransactionId);

            log.info("Sent " + arkSendAmount + " ark to " + contractEntity.getRecipientArkAddress()
                + ", ark transaction id " + arkTransactionId + ", eth transaction " + ethTransactionId);

            transferEntity.setStatus(TransferStatus.COMPLETE);
            transferRepository.save(transferEntity);
            
            log.info("Saved transfer id " + transferEntity.getId() + " to contract " + contractEntity.getId());
        }
        
        return ResponseEntity.ok().build();
    }
}
