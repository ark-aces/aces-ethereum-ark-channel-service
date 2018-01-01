# aces-service-btc-ark-channel
ACES BTC to ARK transfer channel service


## Using Service

```
curl -X POST localhost:9190/contracts \
-H 'Content-type: application/json' \
-d '{
  "recipientArkAddress": "ARNJJruY6RcuYCXcwWsu4bx9kyZtntqeAx"
}' 
```