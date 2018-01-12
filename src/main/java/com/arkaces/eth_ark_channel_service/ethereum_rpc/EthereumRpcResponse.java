package com.arkaces.eth_ark_channel_service.ethereum_rpc;

import lombok.Data;

@Data
public class EthereumRpcResponse<T> {

    private T result;
    private Object error;
    private String id;
}
