from app.config import app_config
from rag.retrieval.retriever import Retriever
from rag.retrieval.rrf import RRF
from rag.dependency.database import document_weaviate

_retrieval_config = app_config.retrieval_config

document_rrf = RRF(
    K=_retrieval_config.document.K, 
    ratio=_retrieval_config.document.ratio
)

conversation_rrf = RRF(
    K=_retrieval_config.conversation.K,
    ratio=_retrieval_config.conversation.ratio
)

retriver = Retriever(
    document_weaviate=document_weaviate,
    adj_limit=_retrieval_config.document.adj_limit
)