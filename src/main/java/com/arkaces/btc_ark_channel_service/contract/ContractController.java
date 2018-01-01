package com.arkaces.btc_ark_channel_service.contract;

import com.arkaces.aces_server.aces_service.contract.Contract;
import com.arkaces.aces_server.aces_service.contract.ContractStatus;
import com.arkaces.aces_server.aces_service.contract.CreateContractRequest;
import com.arkaces.aces_server.aces_service.error.ServiceErrorCodes;
import com.arkaces.aces_server.common.error.NotFoundException;
import com.arkaces.aces_server.common.identifer.IdentifierGenerator;
import lombok.RequiredArgsConstructor;
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
    
    @PostMapping("/contracts")
    public Contract<Results> postContract(@RequestBody CreateContractRequest<Arguments> createContractRequest) {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setCorrelationId(createContractRequest.getCorrelationId());
        contractEntity.setCreatedAt(LocalDateTime.now());
        contractEntity.setId(identifierGenerator.generate());
        contractEntity.setStatus(ContractStatus.EXECUTED);
        contractRepository.save(contractEntity);
        
        // todo: do the stuff!
        
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
