package com.arkaces.btc_ark_channel_service.contract;

import com.arkaces.btc_ark_channel_service.transfer.TransferEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "contracts")
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String id;
    private String correlationId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String recipientArkAddress;
    private String depositBtcAddress;
    private String subscriptionId;
    
    // todo store password encrypted in db
    private String depositBtcPassphrase;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contractEntity")
    private List<TransferEntity> transferEntities = new ArrayList<>();
    
}
