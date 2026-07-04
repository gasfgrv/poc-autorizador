import os
import boto3
from decimal import Decimal
from domain.entities import Transaction
from domain.ports import TransactionRepository


class DynamoDBTransactionRepository(TransactionRepository):
    def __init__(self, table_name: str = None, dynamodb_resource=None):
        self.table_name = table_name or os.environ.get(
            "TABLE_NAME", "tb-autorizador-resposta")
        self.dynamodb = dynamodb_resource or boto3.resource("dynamodb")
        self.table = self.dynamodb.Table(self.table_name)

    def save(self, transaction: Transaction) -> None:
        item = transaction.to_dict()
        serialized_item = self._serialize_floats(item)
        self.table.put_item(Item=serialized_item)

    def _serialize_floats(self, val):
        if isinstance(val, float):
            return Decimal(str(val))
        elif isinstance(val, dict):
            return {k: self._serialize_floats(v) for k, v in val.items()}
        elif isinstance(val, list):
            return [self._serialize_floats(i) for i in val]
        return val
