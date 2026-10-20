from rag.query.retrieval import Retriever

class RetrivalPipeline:
    def __init__(
        self,
        retriever: Retriever
    ):
        self._retriever = retriever
        
    def retrieve(self, query: str) -> dict:
        context, ids = self._retriever.retrieve(query)
        result: dict = {}
        result["context"] = context
        result["related_document_ids"] = ids
        return result