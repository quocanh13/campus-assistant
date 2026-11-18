from uuid import UUID
from rag.database import DocumentWeaviate, ConversationWeaviate
from rag.retrieval.retriever import Retriever
from rag.retrieval.rrf import RRF

class RetrieveDocumentPipeline:
    def __init__(
        self,
        retriever: Retriever,
        document_weaviate: DocumentWeaviate,
        rrf: RRF,
        search_limit: int,
        ranked_limit: int
    ):
        self.retriever = retriever
        self.document_weaviate = document_weaviate
        self.rrf = rrf
        self.search_limit = search_limit
        self.ranked_limit = ranked_limit
        
    def retrieve(
        self, 
        query: str,
        cohort: int | None = None,
        keywords: list[str] = [],
        is_valid = True,
        document_id: UUID | None = None
    ) -> list[dict]:
        
        vector_chunks = self.document_weaviate.dense_search(
            query=query,
            limit=self.search_limit,
            cohort=cohort,
            is_valid=is_valid,
            document_id=document_id
        )
        bm25_chunks = self.document_weaviate.bm25_search(
            query=" ".join(keywords),
            limit=self.search_limit,
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
            
        scores = self.rrf.rerank(chunk_ranks)
        chunks_by_id = { chunk.uuid: chunk for chunk in [*vector_chunks, *bm25_chunks] }
        ranked_chunks = []
        for _ in range(min(self.ranked_limit, len(scores))):
            best_id = max(scores, key=scores.get)
            ranked_chunks.append(chunks_by_id[best_id])
            del scores[best_id]
            
        
        result = self.retriever.retrieve(
            chunks=ranked_chunks
        )
        return result

class RetrieveConversationPipeline:
    def __init__(
        self,
        conversation_weaviate: ConversationWeaviate,
        rrf: RRF,
        search_limit: int,
        ranked_limit: int
    ):
        self.conversation_weaviate = conversation_weaviate
        self.rrf = rrf
        self.search_limit = search_limit
        self.ranked_limit = ranked_limit
    
    def retrieve(
        self,
        query: str
    ):
        title_query_chunks = self.conversation_weaviate.title_search(query=query, limit=self.search_limit)
        content_query_chunks = self.conversation_weaviate.content_search(query=query, limit=self.search_limit)
        
        chunk_ranks: dict[UUID, list[int | None]] = {}
        for rank, chunk in enumerate(title_query_chunks):
            value = chunk_ranks.get(chunk.uuid, [None, None])
            value[0] = rank
            chunk_ranks[chunk.uuid] = value
        for rank, chunk in enumerate(content_query_chunks):
            value = chunk_ranks.get(chunk.uuid, [None, None])
            value[1] = rank
            chunk_ranks[chunk.uuid] = value
            
        scores = self.rrf.rerank(chunk_ranks)
        chunks_by_id = { chunk.uuid: chunk for chunk in [*title_query_chunks, *content_query_chunks] }
        ranked_chunks = []
        for _ in range(min(self.ranked_limit, len(scores))):
            best_id = max(scores, key=scores.get)
            ranked_chunks.append(chunks_by_id[best_id])
            del scores[best_id]
            
        result = []
        for ranked_chunk in ranked_chunks:
            result.append({
                "id" : ranked_chunk.uuid,
                "conversation_id" : ranked_chunk.properties["conversation_id"],
                "title" : ranked_chunk.properties["title"],
                "messages" : [
                    {
                        "role" : ranked_chunk.properties["roles"][i],
                        "content" : ranked_chunk.properties["contents"][i]      
                    } 
                    for i in range(len(ranked_chunk.properties["roles"]))
                ]
            })
        return result