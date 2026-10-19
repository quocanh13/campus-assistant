from config import app_config
from .database import weaviate_db
from .storage import s3_storage
from .ingestion import enricher
from rag.dependency.query import generator, query_processor, retriver
from rag.pipeline.ingestion import IngestionPipeline
from rag.pipeline.generation import GenerationPipeline

_ingestion_config = app_config.ingestion_config

ingestion_pipeline = IngestionPipeline(
    storage=s3_storage,
    database=weaviate_db,
    chunk_size=_ingestion_config.chunk_size,
    enricher=enricher
)

generation_pipeline = GenerationPipeline(
    generator=generator,
    retriever=retriver,
    query_processor=query_processor
)