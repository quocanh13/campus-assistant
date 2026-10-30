from app.config import app_config
from rag.retrieval.retriever import Retriever
from rag.retrieval.rrf import RRF
from rag.dependency.database import weaviate_db

_retrieval_config = app_config.retrieval_config

rrf = RRF(K=_retrieval_config.K, ratio=_retrieval_config.ratio)

retriver = Retriever(
    weaviate_db=weaviate_db,
    adj_limit=_retrieval_config.adj_limit
)