package com.arkaces.eth_ark_channel_service.contract;

import com.arkaces.ApiException;
import com.arkaces.aces_listener_api.AcesListenerApi;
import com.arkaces.aces_server.aces_service.contract.Contract;
import com.arkaces.aces_server.aces_service.contract.ContractStatus;
import com.arkaces.aces_server.aces_service.contract.CreateContractRequest;
import com.arkaces.aces_server.aces_service.error.ServiceErrorCodes;
import com.arkaces.aces_server.common.api_key_generation.ApiKeyGenerator;
import com.arkaces.aces_server.common.error.NotFoundException;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import com.arkaces.eth_ark_channel_service.ethereum.EthereumService;
import io.swagger.client.model.Subscription;
import io.swagger.client.model.SubscriptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractController {

    private final IdentifierGenerator identifierGenerator;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final AcesListenerApi ethereumListener;
    private final String ethEventCallbackUrl;
    private final Integer ethMinConfirmations;
    private final ApiKeyGenerator apiKeyGenerator;
    private final EthereumService ethereumService;

    @PostMapping("/contracts")
    public Contract<Results> postContract(@RequestBody CreateContractRequest<Arguments> createContractRequest) {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setId(identifierGenerator.generate());
        contractEntity.setCorrelationId(createContractRequest.getCorrelationId());
        contractEntity.setStatus(ContractStatus.EXECUTED);
        contractEntity.setCreatedAt(LocalDateTime.now());
        contractEntity.setRecipientArkAddress(createContractRequest.getArguments().getRecipientArkAddress());

        // Generate ethereum wallet for deposits
        String depositEthPassphrase = apiKeyGenerator.generate();
        contractEntity.setDepositEthPassphrase(depositEthPassphrase);

        String depositEthAddress = ethereumService.createAddress(depositEthPassphrase);
        contractEntity.setDepositEthAddress(depositEthAddress);
        log.info("Deposit Eth Address: \"{}\" --- Deposit Eth Passphrase: \"{}\" --- Deposit Eth Private Key: \"{}\"",
                depositEthAddress,
                depositEthPassphrase
        );

        // Subscribe to ethereum listener on deposit ethereum address
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setCallbackUrl(ethEventCallbackUrl);
        subscriptionRequest.setMinConfirmations(ethMinConfirmations);
        subscriptionRequest.setRecipientAddress(depositEthAddress);

        Subscription subscription;
        try {
            subscription = ethereumListener.subscriptionsPost(subscriptionRequest);
        } catch (ApiException e) {
            throw new RuntimeException("Ethereum Listener subscription failed to POST", e);
        }
        contractEntity.setSubscriptionId(subscription.getId());

        contractRepository.save(contractEntity);

        return contractMapper.map(contractEntity);
    }

    @GetMapping("/contracts/{contractId}")
    public Contract<Results> getContract(@PathVariable String contractId) {
        ContractEntity contractEntity = contractRepository.findOneById(contractId);
        if (contractEntity == null) {
            throw new NotFoundException(ServiceErrorCodes.CONTRACT_NOT_FOUND, "Contract not found with id = " + contractId);
        }
        return contractMapper.map(contractEntity);
    }
}
