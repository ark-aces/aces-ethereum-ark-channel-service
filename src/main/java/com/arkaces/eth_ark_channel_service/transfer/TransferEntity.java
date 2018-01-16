package com.arkaces.eth_ark_channel_service.transfer;

import com.arkaces.eth_ark_channel_service.contract.ContractEntity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String id;
    private LocalDateTime createdAt;
    private String status;
    private String ethTransactionId;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethAmount;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethToArkRate;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethFlatFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethPercentFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal ethTotalFee;

    private String arkTransactionId;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkSendAmount;

    @ManyToOne(cascade = CascadeType.ALL)
    private ContractEntity contractEntity;
}
