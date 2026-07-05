# poc-autorizador

Exemplo de payload para a stepfunction:

```json
{
    "orderId": "1234567",
    "customerId": "98766",
    "orderDate": "2024-01-14",
    "amount": 100,
    "nameOnCard": "FIRSTNAME LASTNAME",
    "creditCardNumber": "1234 1234 1234 1234",
    "expiry": "XX/YY",
    "cvv": "123"
}
```

Comando para iniciar todo o fluxo

```sh
awslocal stepfunctions start-execution --state-machine-arn arn:aws:states:us-east-1:000000000000:stateMachine:autorizador-workflow --input "{\"orderId\":\"1234567\",\"customerId\":\"98766\",\"orderDate\":\"2024-01-14\",\"amount\":100,\"nameOnCard\":\"FIRSTNAME LASTNAME\",\"creditCardNumber\":\"1234 1234 1234 1234\",\"expiry\":\"XX/YY\",\"cvv\":\"123\"}"
```