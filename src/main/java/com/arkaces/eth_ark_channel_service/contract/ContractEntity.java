package com.arkaces.eth_ark_channel_service.contract;

import com.arkaces.eth_ark_channel_service.transfer.TransferEntity;
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
    private String depositEthAddress;
    private String depositEthPassphrase; // TODO: Store passphrase encrypted in db
    private String depositEthPrivateKey; // TODO: Store private key encrypted in db
    private String subscriptionId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contractEntity")
    private List<TransferEntity> transferEntities = new ArrayList<>();
}
