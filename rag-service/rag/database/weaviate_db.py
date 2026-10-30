import uuid
import logging
import weaviate
from datetime import datetime
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
        from_cohort: int,
        to_cohort: int,
        keywords: list[str],
        valid_at: datetime,
        expired_at: datetime
    ) -> str:
        id = self.collection.data.insert(properties={
            "file_id" : file_id,
            "content" : content,
            "chunk_index" : chunk_index,
            "from_cohort" : from_cohort,
            "to_cohort" : to_cohort,
            "keywords" : keywords,
            "valid_at" : valid_at,
            "expired_at" : expired_at
        })
        return id

    @staticmethod
    def create_filters(
        cohort: int | None = None,
        is_valid: bool = True
    ):
        current = datetime.now()
        cohort_filter = (
            Filter.by_property("from_cohort").less_or_equal(cohort) & 
            Filter.by_property("to_cohort").greater_or_equal(cohort)
        ) if cohort is not None else None
        
        valid_filter = (
            Filter.by_property("valid_at").less_or_equal(current) &
            Filter.by_property("expired_at").greater_or_equal(current)
        ) if is_valid else None
        
        filters = None
        if cohort_filter is not None and valid_filter is not None:
            filters = cohort_filter & valid_filter
        elif cohort_filter is not None:
            filters = cohort_filter
        elif valid_filter is not None:
            filters = valid_filter
        return filters

    def dense_search(
        self, 
        query: str, 
        limit: int,
        cohort: int | None = None,
        is_valid: bool = True
    ):
        filters = self.create_filters(cohort, is_valid)
        response = self.collection.query.near_text(
            query=query,
            limit=limit,
            target_vector="content_vector",
            filters=filters
        )
        return response.objects

    def bm25_search(
        self,
        query: str,
        limit: int,
        cohort: int | None = None,
        is_valid: bool = True
    ):
        if(query == ""):
            return []
        filters = self.create_filters(cohort, is_valid)
        response = self.collection.query.bm25(
            query=query,
            query_properties=["keywords"],
            limit=limit,
            filters=filters
        )
        return response.objects
   
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



