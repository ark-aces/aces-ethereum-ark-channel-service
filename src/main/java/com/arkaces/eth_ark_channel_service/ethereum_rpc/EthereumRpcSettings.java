package com.arkaces.eth_ark_channel_service.ethereum_rpc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ethereumRpc")
public class EthereumRpcSettings {

    private String username;
    private String password;
}
