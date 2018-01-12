package com.arkaces.eth_ark_channel_service.bitcoin_rpc;

import lombok.Data;

@Data
public class BitcoinRpcResponse<T> {
    private T result;
    private Object error;
    private String id;
}
