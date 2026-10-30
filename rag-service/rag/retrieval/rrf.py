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
        bm25_rank, vector_rank = rank
        score = 0.0
        if bm25_rank is not None:
            score += (1 - ratio)/(K + bm25_rank)
        if vector_rank is not None:
            score += ratio/(K + vector_rank)
        return score
        