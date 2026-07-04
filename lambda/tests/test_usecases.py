from domain.ports import TransactionRepository
from domain.entities import Transaction
from application.usecases import SaveTransactionUseCase


class MockTransactionRepository(TransactionRepository):
    def __init__(self):
        self.saved_transactions = []

    def save(self, transaction: Transaction) -> None:
        self.saved_transactions.append(transaction)


def test_save_transaction_usecase():
    repo = MockTransactionRepository()
    use_case = SaveTransactionUseCase(repo)

    payload = {
        "amount": 200.5,
        "taskToken": "some-token",
        "customer": "John Doe"
    }

    transaction = use_case.execute(payload)

    # Assert repository has the saved transaction
    assert len(repo.saved_transactions) == 1
    assert repo.saved_transactions[0] == transaction
    assert transaction.data["amount"] == 200.5
    assert transaction.data["customer"] == "John Doe"
    assert "taskToken" not in transaction.data
