from decimal import Decimal

from domain.entities import Transaction
from infra.adapters import DynamoDBTransactionRepository


def test_repository_save_converts_floats(mocker):
    mock_table = mocker.MagicMock()
    mock_dynamodb = mocker.MagicMock()
    mock_dynamodb.Table.return_value = mock_table

    repo = DynamoDBTransactionRepository(
        table_name="test-table", dynamodb_resource=mock_dynamodb)

    transaction = Transaction("trans-123", {
        "float_val": 12.34,
        "int_val": 42,
        "str_val": "hello",
        "nested": {
            "nested_float": 5.67,
            "nested_list": [1.2, 3]
        }
    })

    repo.save(transaction)

    mock_table.put_item.assert_called_once()
    saved_item = mock_table.put_item.call_args[1]["Item"]

    assert saved_item["id_transacao"] == "trans-123"
    assert isinstance(saved_item["float_val"], Decimal)
    assert saved_item["float_val"] == Decimal("12.34")
    assert saved_item["int_val"] == 42
    assert saved_item["str_val"] == "hello"
    assert isinstance(saved_item["nested"]["nested_float"], Decimal)
    assert saved_item["nested"]["nested_float"] == Decimal("5.67")
    assert isinstance(saved_item["nested"]["nested_list"][0], Decimal)
    assert saved_item["nested"]["nested_list"][0] == Decimal("1.2")
    assert saved_item["nested"]["nested_list"][1] == 3

