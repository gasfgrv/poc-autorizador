import random
import string
import time


class Transaction:
    def __init__(self, id_transacao: str, data: dict):
        self.id_transacao = id_transacao
        self.data = data

    @classmethod
    def create(cls, payload: dict) -> "Transaction":
        chars = string.ascii_lowercase + string.digits
        random_str = "".join(random.choices(chars, k=6))
        timestamp_ms = int(time.time() * 1000)
        id_transacao = f"transaction_{random_str}_{timestamp_ms}"

        data = {k: v for k, v in payload.items() if k != "taskToken"}
        return cls(id_transacao, data)

    def to_dict(self) -> dict:
        item = {"id_transacao": self.id_transacao}
        item.update(self.data)
        return item
