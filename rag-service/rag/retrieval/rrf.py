from uuid import UUID

class RRF:
    def __init__(
        self,
        K: int,
        ratio: float
    ):
        self._K = K
        self._ratio = ratio
    
    def rerank(
        self,
        input: dict[UUID, tuple[int | None, int | None]]
    ):
        scores: dict[UUID, float] = {}
        for id, rank in input.items():
            scores[id] = self._get_score(self._K, self._ratio, rank)
        return scores
    
    @staticmethod
    def _get_score(
        K: int,
        ratio: float,
        rank: tuple[int | None, int | None]
    ):
        first_rank, second_rank = rank
        score = 0.0
        if first_rank is not None:
            score += ratio/(K + first_rank)
        if second_rank is not None:
            score += (1 - ratio)/(K + second_rank)
        return score
        