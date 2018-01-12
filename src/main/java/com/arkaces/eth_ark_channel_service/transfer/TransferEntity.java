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
    private String btcTransactionId;

    @Column(precision = 20, scale = 8)
    private BigDecimal btcAmount;

    @Column(precision = 20, scale = 8)
    private BigDecimal btcToArkRate;

    @Column(precision = 20, scale = 8)
    private BigDecimal btcFlatFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal btcPercentFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal btcTotalFee;

    @Column(precision = 20, scale = 8)
    private BigDecimal arkSendAmount;

    private String arkTransactionId;

    @ManyToOne(cascade = CascadeType.ALL)
    private ContractEntity contractEntity;
}
