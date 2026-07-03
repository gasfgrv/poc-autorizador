import json
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def handler(event, context):
    logger.info("Lambda 'lambda-response' invoked with event: %s", json.dumps(event))
    
    # Exemplo de lógica para responder ao fluxo de transações
    transaction_id = event.get("id_transacao", "unknown-id")
    
    response = {
        "status": "APPROVED",
        "id_transacao": transaction_id,
        "message": "Transação autorizada com sucesso"
    }
    
    return {
        "statusCode": 200,
        "body": json.dumps(response)
    }
