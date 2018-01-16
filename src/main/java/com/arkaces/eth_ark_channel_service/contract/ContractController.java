package com.arkaces.eth_ark_channel_service.contract;

import com.arkaces.ApiException;
import com.arkaces.aces_listener_api.AcesListenerApi;
import com.arkaces.aces_server.aces_service.contract.Contract;
import com.arkaces.aces_server.aces_service.contract.ContractStatus;
import com.arkaces.aces_server.aces_service.contract.CreateContractRequest;
import com.arkaces.aces_server.aces_service.error.ServiceErrorCodes;
import com.arkaces.aces_server.common.error.NotFoundException;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import io.swagger.client.model.Subscription;
import io.swagger.client.model.SubscriptionRequest;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.core.ECKey;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractController {

    private final IdentifierGenerator identifierGenerator;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final AcesListenerApi ethereumListener;

    @PostMapping("/contracts")
    public Contract<Results> postContract(@RequestBody CreateContractRequest<Arguments> createContractRequest) {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setCorrelationId(createContractRequest.getCorrelationId());
        contractEntity.setCreatedAt(LocalDateTime.now());
        contractEntity.setId(identifierGenerator.generate());
        contractEntity.setStatus(ContractStatus.EXECUTED);

        // Generate ethereum wallet for deposits
        ECKey key = new ECKey();
        String depositEthAddress = Hex.toHexString(key.getPubKeyHash());
        contractEntity.setDepositEthAddress(depositEthAddress);
        String depositEthPrivateKey = Hex.toHexString(key.getPrivKeyBytes());
        contractEntity.setDepositEthPrivateKey(depositEthPrivateKey);

        // Subscribe to ethereum listener on deposit ethereum address
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setCallbackUrl(depositEthAddress);
        subscriptionRequest.setMinConfirmations(2);
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
