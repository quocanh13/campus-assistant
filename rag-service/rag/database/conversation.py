import uuid
import logging
import weaviate
from datetime import datetime, timezone, timedelta
from weaviate.classes.query import Filter, MetadataQuery, Sort

_logger = logging.getLogger(__name__)

class ConversationWeaviate:
    TIMEZONE = timezone(timedelta(hours=7))
    MAX_RESULTS = 100000
    
    def __init__(
        self,
        cluster_url: str,
        api_key: str,
        collection: str
    ):
        try:
            self.client: weaviate.WeaviateClient = weaviate.connect_to_weaviate_cloud(
                cluster_url=cluster_url,
                auth_credentials=weaviate.auth.AuthApiKey(api_key=api_key)
            )
        except Exception:
            _logger.exception("Cannot connect to Weaviate")
            raise
        if not self.client.collections.exists(collection):
            _logger.critical(f"Collection {collection} does not exist")
            raise Exception(f"Collection {collection} does not exist")
        else:
            self.collection = self.client.collections.get(collection)

    def insert(
        self,
        conversation_id: uuid.UUID,
        title: str,
        roles: list[str],
        contents: list[str]
    ) -> str:
        
        id = self.collection.data.insert(properties={
            "conversation_id" : conversation_id,
            "title" : title,
            "roles" : roles,
            "contents" : contents
        })
        return id

    def title_search(
        self, 
        query: str, 
        limit: int
    ):
        response = self.collection.query.near_text(
            query=query,
            limit=limit,
            target_vector="title_vector",
        )
        return response.objects

    def content_search(
        self, 
        query: str, 
        limit: int
    ):
        response = self.collection.query.near_text(
            query=query,
            limit=limit,
            target_vector="content_vector",
        )
        return response.objects      

    def delete_by_conversation_id(
        self,
        conversation_id: uuid.UUID
    ):
        filter = Filter.by_property("conversation_id").equal(conversation_id)
        self.collection.data.delete_many(
            where=filter
        )
        
    def delete_all(self):
        filter = Filter.by_property("conversation_ids").not_equal(uuid.uuid4())
        self.collection.data.delete_many(
            where=filter
        )
        
    def close(self):
        self.client.close()



