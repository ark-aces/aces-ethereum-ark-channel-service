package com.arkaces.eth_ark_channel_service.transfer;

import ark_java_client.ArkClient;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import com.arkaces.aces_server.aces_service.notification.NotificationService;
import com.arkaces.eth_ark_channel_service.Constants;
import com.arkaces.eth_ark_channel_service.FeeSettings;
import com.arkaces.eth_ark_channel_service.ServiceArkAccountSettings;
import com.arkaces.eth_ark_channel_service.ark.ArkSatoshiService;
import com.arkaces.eth_ark_channel_service.contract.ContractEntity;
import com.arkaces.eth_ark_channel_service.contract.ContractRepository;
import com.arkaces.eth_ark_channel_service.ethereum.EthereumWeiService;
import com.arkaces.eth_ark_channel_service.exchange_rate.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestController
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
    private final EthereumWeiService ethereumWeiService;
    private final NotificationService notificationService;
    private final BigDecimal lowCapacityThreshold;

    @PostMapping("/ethereumEvents")
    public ResponseEntity<Void> handleEthereumEvent(@RequestBody EthereumEventPayload eventPayload) {
        String ethTransactionId = eventPayload.getTransactionId();
        EthereumTransaction transaction = eventPayload.getTransaction();

        log.info("Received ethereum event: {} -> {}", ethTransactionId, transaction.toString());

        String subscriptionId = eventPayload.getSubscriptionId();
        ContractEntity contractEntity = contractRepository.findOneBySubscriptionId(subscriptionId);
        if (contractEntity != null) {
            log.info("Matched event for contract id {}, eth transaction id {}", contractEntity.getId(), ethTransactionId);

            TransferEntity transferEntity = new TransferEntity();
            String transferId = identifierGenerator.generate();
            transferEntity.setId(transferId);
            transferEntity.setCreatedAt(LocalDateTime.now());
            transferEntity.setEthTransactionId(ethTransactionId);
            transferEntity.setContractEntity(contractEntity);

            // Get ETH amount from transaction
            BigDecimal ethAmount = ethereumWeiService.toEther(Long.decode(transaction.getValue()));
            transferEntity.setEthAmount(ethAmount);

            BigDecimal ethToArkRate = exchangeRateService.getRate("ETH", "ARK");
            transferEntity.setEthToArkRate(ethToArkRate);

            // Set fees
            transferEntity.setEthFlatFee(feeSettings.getEthFlatFee());
            transferEntity.setEthPercentFee(feeSettings.getEthPercentFee());

            BigDecimal percentFee = feeSettings.getEthPercentFee().divide(new BigDecimal("100.00"), 8, BigDecimal.ROUND_HALF_UP);
            BigDecimal ethTotalFeeAmount = ethAmount.multiply(percentFee).add(feeSettings.getEthFlatFee());
            transferEntity.setEthTotalFee(ethTotalFeeAmount);

            // Calculate send ark amount
            BigDecimal arkSendAmount = ethAmount.multiply(ethToArkRate).setScale(8, RoundingMode.HALF_DOWN)
                    .subtract(Constants.ARK_TRANSACTION_FEE);
            if (arkSendAmount.compareTo(Constants.ARK_TRANSACTION_FEE) <= 0) {
                arkSendAmount = BigDecimal.ZERO;
            }
            transferEntity.setArkSendAmount(arkSendAmount);

            transferEntity.setStatus(TransferStatus.NEW.getStatus());

            transferRepository.save(transferEntity);

            // Check that service has enough ark to send
            SimpleRetryPolicy policy = new SimpleRetryPolicy(5, Collections.singletonMap(Exception.class, true));
            RetryTemplate template = new RetryTemplate();
            template.setRetryPolicy(policy);
            BigDecimal serviceAvailableArk;
            try {
                serviceAvailableArk = template.execute((RetryCallback<BigDecimal, Exception>) context ->
                        arkSatoshiService.toArk(Long.parseLong(
                                arkClient.getBalance(serviceArkAccountSettings.getAddress())
                                        .getBalance()))
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse value", e);
            }

            if (arkSendAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (arkSendAmount.compareTo(serviceAvailableArk) <= 0) {
                    // Send ark transaction
                    Long arkSendSatoshis = arkSatoshiService.toSatoshi(arkSendAmount);
                    String arkTransactionId = arkClient.broadcastTransaction(
                            contractEntity.getRecipientArkAddress(),
                            arkSendSatoshis,
                            null,
                            serviceArkAccountSettings.getPassphrase(),
                            10
                    );

                    // Check if ark transaction was successful
                    if (arkTransactionId != null) {
                        transferEntity.setArkTransactionId(arkTransactionId);

                        log.info("Sent {} ARK to {}, ark transaction id {}, eth transaction id {}",
                                arkSendAmount.toPlainString(),
                                contractEntity.getRecipientArkAddress(),
                                arkTransactionId,
                                ethTransactionId
                        );

                        transferEntity.setStatus(TransferStatus.COMPLETE.getStatus());

                        notificationService.notifySuccessfulTransfer(
                                transferEntity.getContractEntity().getId(),
                                transferEntity.getId()
                        );


                    } else {
                        String message = "Failed to send" + arkSendAmount.toPlainString() +
                        " ARK to " + contractEntity.getRecipientArkAddress()+ ", eth transaction id " + ethTransactionId;
                        log.error(message);

                        transferEntity.setStatus(TransferStatus.FAILED.getStatus());

                        notificationService.notifyFailedTransfer(
                                transferEntity.getContractEntity().getId(),
                                transferEntity.getId(),
                                message
                        );

                    }
                } else {
                    String message = "Failed to send transfer " + transferId + " due to insufficient service ark: available = "
                            + serviceAvailableArk + ", send amount: " + arkSendAmount;
                    log.warn(message);
                    transferEntity.setStatus(TransferStatus.FAILED.getStatus());

                    notificationService.notifyFailedTransfer(
                            transferEntity.getContractEntity().getId(),
                            transferEntity.getId(),
                            message
                    );
                }
            } else {
                transferEntity.setStatus(TransferStatus.COMPLETE.getStatus());
                notificationService.notifySuccessfulTransfer(
                        transferEntity.getContractEntity().getId(),
                        transferEntity.getId()
                );
            }

            transferRepository.save(transferEntity);

            log.info("Saved transfer id {} to contract {}.", transferEntity.getId(), contractEntity.getId());

        }

        return ResponseEntity.ok().build();
    }
}
