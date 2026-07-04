from abc import ABC, abstractmethod

from domain.entities import Transaction


class TransactionRepository(ABC):
    @abstractmethod
    def save(self, transaction: Transaction) -> None:
        pass
