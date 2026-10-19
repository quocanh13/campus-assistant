from rag.query.query_processing import QueryProcessor
from rag.query.retrieval import Retriever
from rag.query.generation import Generator

class GenerationPipeline:
    def __init__(
        self,
        query_processor: QueryProcessor,
        retriever: Retriever,
        generator: Generator
    ):
        self._query_processor = query_processor
        self._retriever = retriever
        self._generator = generator
        
    def generate(self, query: str) -> dict[str]:
        processed_query = self._query_processor.process(query)
        context, ids = self._retriever.retrieve(processed_query)
        answer = self._generator.generate_answer(query, context)
        result: dict = {}
        result["processed_query"] = processed_query
        result["answer"] = answer
        result["context"] = context
        result["related_document_ids"] = ids
        return result
    