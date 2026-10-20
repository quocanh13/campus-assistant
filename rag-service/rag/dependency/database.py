from app.config import app_config
from ..database.weaviate_db import WeaviateDB

_weaviate_config = app_config.weaviate_config

weaviate_db = WeaviateDB(
    cluster_url=_weaviate_config.url,
    api_key=_weaviate_config.api_key,
    collection=_weaviate_config.collection
)