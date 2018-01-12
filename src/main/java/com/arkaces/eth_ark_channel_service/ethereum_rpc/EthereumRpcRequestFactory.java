package com.arkaces.eth_ark_channel_service.ethereum_rpc;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EthereumRpcRequestFactory {

    public EthereumRpcRequest create(String method, List<Object> params) {
        EthereumRpcRequest request = new EthereumRpcRequest();
        request.setMethod(method);
        request.setParams(params);
        return request;
    }
}
