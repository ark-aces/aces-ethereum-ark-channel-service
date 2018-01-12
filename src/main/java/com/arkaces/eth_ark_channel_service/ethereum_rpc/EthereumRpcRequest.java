package com.arkaces.eth_ark_channel_service.ethereum_rpc;

import lombok.Data;

import java.util.List;

@Data
public class EthereumRpcRequest {

    private String jsonrpc = "1.0";
    private String id = "curltext";
    private String method;
    private List<Object> params;
}
