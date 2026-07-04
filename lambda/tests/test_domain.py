import re

from domain.entities import Transaction


def test_transaction_creation():
    payload = {
        "amount": 150,
        "taskToken": "1c1d18c7-c542-4d48-a222-24e65883a8dc",
        "card_brand": "visa"
    }

    transaction = Transaction.create(payload)

    pattern = r"^transaction_[a-z0-9]{6}_\d+$"
    assert re.match(pattern, transaction.id_transacao) is not None

    assert "amount" in transaction.data
    assert "card_brand" in transaction.data
    assert "taskToken" not in transaction.data
    assert transaction.data["amount"] == 150
    assert transaction.data["card_brand"] == "visa"


def test_transaction_to_dict():
    payload = {
        "amount": 150,
        "taskToken": "12345"
    }

    transaction = Transaction.create(payload)
    result_dict = transaction.to_dict()

    assert "id_transacao" in result_dict
    assert result_dict["id_transacao"] == transaction.id_transacao
    assert result_dict["amount"] == 150
    assert "taskToken" not in result_dict
