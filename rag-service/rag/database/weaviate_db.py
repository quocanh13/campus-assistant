import uuid
import logging
import weaviate
from enum import Enum
from weaviate.classes.query import Filter, MetadataQuery

_logger = logging.getLogger(__name__)

class WeaviateDB:
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
        file_id: uuid.UUID,
        content: str,
        chunk_index: int,
        document_type: str,
        cohort: int,
        keyword: str
    ) -> str:
        id = self.collection.data.insert(properties={
            "file_id" : file_id,
            "content" : content,
            "chunk_index" : chunk_index,
            "document_type" : document_type,
            "cohort" : cohort,
            "keyword" : keyword
        })
        return id

    def dense_search(self, query: str, limit: int):
        response = self.collection.query.near_text(
            query=query,
            limit=limit,
            return_metadata=MetadataQuery(distance=True)
        )
        return response.objects

    def hybrid_search(self, metadata: dict, limit: int):
        response = self.collection.query.hybrid(
            query=query,
            limit=limit,
            return_metadata=MetadataQuery(distance=True)
        )
        
    def find_by_chunk_index(
        self, 
        file_id: uuid.UUID, 
        index: tuple[int, int] | list[tuple[int, int]]
    ):
        if(isinstance(index, tuple)):
            index = [index]
        result = []
        for start, end in index:
            filters = (
                Filter.by_property("file_id").equal(file_id) &
                Filter.by_property("chunk_index").greater_or_equal(start) &
                Filter.by_property("chunk_index").less_or_equal(end)
            )
            response = self.collection.query.fetch_objects(
                filters=filters
            )
            result.extend(response.objects)
        return result        

    def delete_by_file_id(
        self,
        file_id: uuid.UUID
    ):
        filter = Filter.by_property("file_id").equal(file_id)
        self.collection.data.delete_many(
            where=filter
        )
        
    def delete_all(self):
        filter = Filter.by_property("file_id").not_equal(uuid.uuid4())
        self.collection.data.delete_many(
            where=filter
        )
        
    def close(self):
        self.client.close()



