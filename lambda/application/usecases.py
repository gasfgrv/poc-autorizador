from domain.entities import Transaction
from domain.ports import TransactionRepository


class SaveTransactionUseCase:
    def __init__(self, repo: TransactionRepository):
        self.repository = repo

    def execute(self, payload: dict) -> Transaction:
        transaction = Transaction.create(payload)
        self.repository.save(transaction)
        return transaction
