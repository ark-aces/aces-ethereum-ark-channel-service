package com.arkaces.eth_ark_channel_service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "fees")
public class FeeSettings {
    private BigDecimal ethFlatFee;
    private BigDecimal ethPercentFee;
}
