import json
import logging

from application.usecases import SaveTransactionUseCase
from infra.adapters import DynamoDBTransactionRepository

logger = logging.getLogger()
logger.setLevel(logging.INFO)

_use_case = None


def get_use_case() -> SaveTransactionUseCase:
    global _use_case

    if _use_case is None:
        repository = DynamoDBTransactionRepository()
        _use_case = SaveTransactionUseCase(repository)

    return _use_case


def handler(event, _) -> dict:
    logger.info("Lambda invoked with event: %s", json.dumps(event))

    try:
        payload = event

        if isinstance(event, str):
            payload = json.loads(event)
        elif isinstance(event, dict) and "body" in event and isinstance(event["body"], str):
            payload = json.loads(event["body"])

        use_case = get_use_case()
        transaction = use_case.execute(payload)

        response_body = {
            "message": "Transação registrada com sucesso",
            "id_transacao": transaction.id_transacao,
            "data": transaction.to_dict()
        }

        logger.info("Successfully stored transaction %s", transaction.id_transacao)

        return {
            "statusCode": 200,
            "body": json.dumps(response_body, default=str)
        }
    except Exception as e:
        logger.exception("Error processing transaction authorization")
        return {
            "statusCode": 500,
            "body": json.dumps({
                "error": "Erro interno ao processar transação",
                "details": str(e)
            })
        }
