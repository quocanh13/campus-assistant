from app.config import app_config
from rag.database import DocumentWeaviate, ConversationWeaviate

_document_weaviate_config = app_config.document_weaviate_config
_conversation_weaviate_config = app_config.conversation_weaviate_config

document_weaviate = DocumentWeaviate(
    cluster_url=_document_weaviate_config.url,
    api_key=_document_weaviate_config.api_key,
    collection=_document_weaviate_config.collection
)

conversation_weaviate = ConversationWeaviate(
    cluster_url=_conversation_weaviate_config.url,
    api_key=_conversation_weaviate_config.api_key,
    collection=_conversation_weaviate_config.collection
)