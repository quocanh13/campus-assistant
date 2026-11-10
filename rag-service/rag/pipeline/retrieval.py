from uuid import UUID
from rag.database.weaviate_db import WeaviateDB
from rag.retrieval.retriever import Retriever
from rag.retrieval.rrf import RRF

class RetrivalPipeline:
    def __init__(
        self,
        retriever: Retriever,
        weaviate_db: WeaviateDB,
        rrf: RRF,
        search_limit: int,
        ranked_limit: int
    ):
        self._retriever = retriever
        self._weaviate_db = weaviate_db
        self._rrf = rrf
        self._search_limit = search_limit
        self._ranked_limit = ranked_limit
        
    def retrieve(
        self, 
        query: str,
        cohort: int | None = None,
        keywords: list[str] = [],
        is_valid = True,
        document_id: UUID | None = None
    ) -> list[dict]:
        
        vector_chunks = self._weaviate_db.dense_search(
            query=query,
            limit=self._search_limit,
            cohort=cohort,
            is_valid=is_valid,
            document_id=document_id
        )
        bm25_chunks = self._weaviate_db.bm25_search(
            query=" ".join(keywords),
            limit=self._search_limit,
            cohort=cohort,
            is_valid=is_valid,
            document_id=document_id
        )
        
        chunk_ranks: dict[UUID, list[int | None]] = {}
        for rank, chunk in enumerate(vector_chunks):
            value = chunk_ranks.get(chunk.uuid, [None, None])
            value[0] = rank
            chunk_ranks[chunk.uuid] = value
        for rank, chunk in enumerate(bm25_chunks):
            value = chunk_ranks.get(chunk.uuid, [None, None])
            value[1] = rank
            chunk_ranks[chunk.uuid] = value
            
        scores = self._rrf.rerank(chunk_ranks)
        chunks_by_id = { chunk.uuid: chunk for chunk in [*vector_chunks, *bm25_chunks] }
        ranked_chunks = []
        for _ in range(min(self._ranked_limit, len(scores))):
            best_id = max(scores, key=scores.get)
            ranked_chunks.append(chunks_by_id[best_id])
            del scores[best_id]
            
        
        result = self._retriever.retrieve(
            chunks=ranked_chunks
        )
        return result
