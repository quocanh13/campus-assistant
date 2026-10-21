from app.config import app_config
from rag.query.generation import Generator
from rag.query.query_processing import QueryProcessor
from rag.query.retrieval import Retriever
from rag.dependency.database import weaviate_db

_generation_config = app_config.generation_config
_retrieval_config = app_config.retrieval_config
_query_processing_config = app_config.query_processing_config

query_processor = QueryProcessor(
    model=_query_processing_config.model,
    api_key=_query_processing_config.api_key
)

generator = Generator(
    model=_generation_config.model,
    api_key=_generation_config.api_key
)

retriver = Retriever(
    weaviate_db=weaviate_db,
    limit=_retrieval_config.chunk_limit,
    adj_limit=_retrieval_config.adj_limit
)