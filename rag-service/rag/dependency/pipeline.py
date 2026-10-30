from app.config import app_config
from .database import weaviate_db
from .storage import s3_storage
from .ingestion import enricher
from rag.dependency.retrieval import *
from rag.pipeline.ingestion import IngestionPipeline
from rag.pipeline.retrieval import RetrivalPipeline, RRF

_ingestion_config = app_config.ingestion_config
_retrieval_config = app_config.retrieval_config

ingestion_pipeline = IngestionPipeline(
    storage=s3_storage,
    database=weaviate_db,
    chunk_size=_ingestion_config.chunk_size,
    enricher=enricher
)

retrieval_pipeline = RetrivalPipeline(
    retriever=retriver,
    rrf=rrf,
    weaviate_db=weaviate_db,
    search_limit=_retrieval_config.search_limit,
    ranked_limit=_retrieval_config.ranked_limit
)