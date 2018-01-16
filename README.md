# aces-service-ethereum-ark-channel-service

ACES ETH to ARK transfer channel service

## Using Service

Get service info:

```bash
curl http://localhost:9190/
```

```json
{
  "name" : "Aces ETH-ARK Channel Service",
  "description" : "ACES ETH to ARK Channel service for transferring ETH to ARK",
  "version" : "1.0.0",
  "websiteUrl" : "https://arkaces.com",
  "instructions" : "After this contract is executed, any ETH sent to depositEthAddress will be exchanged for ARK and sent directly to the given recipientArkAddress less service fees.\n",
  "flatFee" : "0",
  "percentFee" : "1.00%",
  "inputSchema" : {
    "type" : "object",
    "properties" : {
      "recipientArkAddress" : {
        "type" : "string"
      }
    },
    "required" : [ "recipientArkAddress" ]
  },
  "outputSchema" : {
    "type" : "object",
    "properties" : {
      "depositEthAddress" : {
        "type" : "string"
      },
      "recipientArkAddress" : {
        "type" : "string"
      },
      "transfers" : {
        "type" : "array",
        "properties" : {
          "id" : {
            "type" : "string"
          },
          "createdAt" : {
            "type" : "string"
          },
          "status" : {
            "type" : "string"
          },
          "ethTransactionId" : {
            "type" : "string"
          },
          "ethAmount" : {
            "type" : "string"
          },
          "ethToArkRate" : {
            "type" : "string"
          },
          "ethFlatFee" : {
            "type" : "string"
          },
          "ethPercentFee" : {
            "type" : "string"
          },
          "ethTotalFee" : {
            "type" : "string"
          },
          "arkTransactionId" : {
            "type" : "string"
          },
          "arkSendAmount" : {
            "type" : "string"
          }
        }
      }
    }
  }
}
```

Create a new Service Contract:

```bash
curl -X POST localhost:9190/contracts \
-H 'Content-type: application/json' \
-d '{
  "recipientArkAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx"
}' 
```

```json
{
  "id": "abe05cd7-40c2-4fb0-a4a7-8d2f76e74978",
  "createdAt": "2017-07-04T21:59:38.129Z",
  "correlationId": "4aafe9-4a40-a7fb-6e788d2497f7",
  "status": "executed",
  "results": {
    "recipientArkAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx",
    "depositEthAddress": "5b83337a5af30bba26a55830a7d0ccf69114137ff699a3d718699ba1f498d77b",
    "transfers": []
  }
}
```

Get Contract information after sending ETH funds to `depositEthAddress`:

```bash
curl -X GET http://localhost:9190/contracts/{id}
```

```json
{
  "id": "abe05cd7-40c2-4fb0-a4a7-8d2f76e74978",
  "createdAt": "2017-07-04T21:59:38.129Z",
  "correlationId": "4aafe9-4a40-a7fb-6e788d2497f7",
  "status": "executed",
  "results": {
    "recipientArkAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx",
    "depositEthAddress": "5b83337a5af30bba26a55830a7d0ccf69114137ff699a3d718699ba1f498d77b",
    "transfers": [
      {
        "id": "fa046b0e-7b05-4a2d-a4c9-168951df3b90",
        "createdAt": "2017-07-05T21:00:38.457Z",
        "status": "complete",
        "ethTransactionId": "49f55381c5c3c70f96e848df53ab7f9ae9881dbb8eb43e8f91f642018bf1258f",
        "ethAmount": "1.00000",
        "ethToArkRate": "2027.58000",
        "ethFlatFee": "0.00000",
        "ethPercentFee": "1.00000",
        "ethTotalFee": "0.01000",
        "arkTransactionId": "49f55381c5c3c70f96e848df53ab7f9ae9881dbb8eb43e8f91f642018bf1258f",
        "arkSendAmount": "2007.30420"
      }
    ]
  }
}
```
