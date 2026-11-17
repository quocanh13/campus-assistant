from app.config import app_config
from .database import document_weaviate, conversation_weaviate
from .storage import s3_storage
from .ingestion import enricher
from rag.dependency.retrieval import *
from rag.pipeline.ingestion import IngestDocumentPipeline, IngestConversationPipeline
from rag.pipeline.retrieval import RetrieveDocumentPipeline, RetrieveConversationPipeline

_ingestion_config = app_config.ingestion_config
_retrieval_config = app_config.retrieval_config

ingest_document_pipeline = IngestDocumentPipeline(
    storage=s3_storage,
    document_weaviate=document_weaviate,
    chunk_size=_ingestion_config.chunk_size,
    enricher=enricher
)

retrieve_document_pipeline = RetrieveDocumentPipeline(
    retriever=retriver,
    rrf=document_rrf,
    document_weaviate=document_weaviate,
    search_limit=_retrieval_config.document.search_limit,
    ranked_limit=_retrieval_config.document.ranked_limit
)

ingest_conversation_pipeline = IngestConversationPipeline(
    conversation_weaviate=conversation_weaviate
)
retrieve_conversation_pipeline = RetrieveConversationPipeline(
    conversation_weaviate=conversation_weaviate,
    rrf=conversation_rrf,
    search_limit=_retrieval_config.conversation.search_limit,
    ranked_limit=_retrieval_config.conversation.ranked_limit
)