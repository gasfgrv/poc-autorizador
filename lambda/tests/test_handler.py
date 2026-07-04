import json
from unittest.mock import MagicMock
import index
from domain.entities import Transaction


def test_handler_success(monkeypatch):
    mock_transaction = Transaction(
        "transaction_abc123_123456789", {"amount": 150})

    mock_use_case = MagicMock()
    mock_use_case.execute.return_value = mock_transaction

    monkeypatch.setattr(index, "get_use_case", lambda: mock_use_case)

    event = {
        "amount": 150,
        "taskToken": "1c1d18c7-c542-4d48-a222-24e65883a8dc"
    }

    response = index.handler(event, None)

    assert response["statusCode"] == 200
    body = json.loads(response["body"])
    assert body["message"] == "Transação registrada com sucesso"
    assert body["id_transacao"] == "transaction_abc123_123456789"
    assert body["data"]["amount"] == 150

    mock_use_case.execute.assert_called_once_with(event)


def test_handler_success_string_event(monkeypatch):
    mock_transaction = Transaction(
        "transaction_xyz789_987654321", {"amount": 300})
    mock_use_case = MagicMock()
    mock_use_case.execute.return_value = mock_transaction

    monkeypatch.setattr(index, "get_use_case", lambda: mock_use_case)

    event_str = '{"amount": 300, "taskToken": "some-token"}'

    response = index.handler(event_str, None)

    assert response["statusCode"] == 200
    body = json.loads(response["body"])
    assert body["id_transacao"] == "transaction_xyz789_987654321"

    mock_use_case.execute.assert_called_once_with(
        {"amount": 300, "taskToken": "some-token"})


def test_handler_exception(monkeypatch):
    mock_use_case = MagicMock()
    mock_use_case.execute.side_effect = Exception("Database failure")

    monkeypatch.setattr(index, "get_use_case", lambda: mock_use_case)

    response = index.handler({"amount": 100}, None)

    assert response["statusCode"] == 500
    body = json.loads(response["body"])
    assert "error" in body
    assert body["details"] == "Database failure"
