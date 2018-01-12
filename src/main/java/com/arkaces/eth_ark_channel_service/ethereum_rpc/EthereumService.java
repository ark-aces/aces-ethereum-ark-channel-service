package com.arkaces.eth_ark_channel_service.ethereum_rpc;

import com.arkaces.aces_server.common.json.NiceObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EthereumService {

    private final RestTemplate ethereumRpcRestTemplate;

    private final EthereumRpcRequestFactory ethereumRpcRequestFactory = new EthereumRpcRequestFactory();

    private final NiceObjectMapper objectMapper = new NiceObjectMapper(new ObjectMapper());

    public String getNewAddress() {
        HttpEntity<String> blockHashRequestEntity = getRequestEntity("getnewaddress", new ArrayList<>());
        return ethereumRpcRestTemplate
            .exchange(
                "/",
                HttpMethod.POST,
                blockHashRequestEntity,
                new ParameterizedTypeReference<EthereumRpcResponse<String>>() {}
            )
            .getBody()
            .getResult();
    }

    public String getPrivateKey(String address) {
        HttpEntity<String> blockHashRequestEntity = getRequestEntity("dumpprivkey", Collections.singletonList(address));
        return ethereumRpcRestTemplate
            .exchange(
                "/",
                HttpMethod.POST,
                blockHashRequestEntity,
                new ParameterizedTypeReference<EthereumRpcResponse<String>>() {}
            )
            .getBody()
            .getResult();
    }

    public JsonNode getTransaction(String transactionId) {
        HttpEntity<String> blockHashRequestEntity = getRequestEntity("gettransaction", Collections.singletonList(transactionId));
        return ethereumRpcRestTemplate
            .exchange(
                "/",
                HttpMethod.POST,
                blockHashRequestEntity,
                new ParameterizedTypeReference<EthereumRpcResponse<JsonNode>>() {}
            )
            .getBody()
            .getResult();
    }

    private HttpEntity<String> getRequestEntity(String method, List<Object> params) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/plain");

        EthereumRpcRequest ethereumRpcRequest = ethereumRpcRequestFactory.create(method, params);
        String body = objectMapper.writeValueAsString(ethereumRpcRequest);

        return new HttpEntity<>(body, headers);
    }
}
