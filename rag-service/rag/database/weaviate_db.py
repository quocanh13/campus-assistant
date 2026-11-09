import uuid
import logging
import weaviate
from datetime import datetime, timezone, timedelta
from weaviate.classes.query import Filter, MetadataQuery, Sort

_logger = logging.getLogger(__name__)

class WeaviateDB:
    TIMEZONE = timezone(timedelta(hours=7))
    
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
        document_id: uuid.UUID,
        document_name: str,
        content: str,
        chunk_index: int,
        from_cohort: int,
        to_cohort: int,
        keywords: list[str],
        valid_at: datetime,
        expired_at: datetime
    ) -> str:
        
        id = self.collection.data.insert(properties={
            "document_id" : document_id,
            "document_name" : document_name,
            "content" : content,
            "chunk_index" : chunk_index,
            "from_cohort" : from_cohort,
            "to_cohort" : to_cohort,
            "keywords" : keywords,
            "valid_at" : valid_at.replace(tzinfo=self.TIMEZONE),
            "expired_at" : expired_at.replace(tzinfo=self.TIMEZONE)
        })
        return id

    @staticmethod
    def create_filters(
        cohort: int | None,
        is_valid: bool,
        document_id: uuid.UUID | None
    ):
        current = datetime.now(tz=WeaviateDB.TIMEZONE)
        cohort_filter = (
            Filter.by_property("from_cohort").less_or_equal(cohort) & 
            Filter.by_property("to_cohort").greater_or_equal(cohort)
        ) if cohort is not None else None
        
        valid_filter = (
            Filter.by_property("valid_at").less_or_equal(current) &
            Filter.by_property("expired_at").greater_or_equal(current)
        ) if is_valid else None
        
        document_id_filter = Filter.by_property("document_id").equal(document_id) if document_id else None
        
        filter_list = [cohort_filter, valid_filter, document_id_filter]
        filters = None
        for filter in filter_list:
            if filter is not None:
                if filters is None: filters = filter
                else: filters = filters & filter
                    
        return filters

    def dense_search(
        self, 
        query: str, 
        limit: int,
        cohort: int | None = None,
        is_valid: bool = True,
        document_id: uuid.UUID | None = None
    ):
        filters = self.create_filters(cohort, is_valid, document_id)
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
        is_valid: bool = True,
        document_id: uuid.UUID | None = None
    ):
        if(query == ""):
            return []
        filters = self.create_filters(cohort, is_valid, document_id)
        response = self.collection.query.bm25(
            query=query,
            query_properties=["keywords"],
            limit=limit,
            filters=filters
        )
        return response.objects
   
    def find_by_chunk_index(
        self, 
        document_id: uuid.UUID, 
        index: tuple[int, int] | list[tuple[int, int]]
    ):
        if(isinstance(index, tuple)):
            index = [index]
        result = []
        for start, end in index:
            filters = (
                Filter.by_property("document_id").equal(document_id) &
                Filter.by_property("chunk_index").greater_or_equal(start) &
                Filter.by_property("chunk_index").less_or_equal(end)
            )
            response = self.collection.query.fetch_objects(
                filters=filters,
                sort=Sort.by_property("chunk_index", ascending=True)
            )
            result.extend(response.objects)
        return result        

    def delete_by_document_id(
        self,
        document_id: uuid.UUID
    ):
        filter = Filter.by_property("document_id").equal(document_id)
        self.collection.data.delete_many(
            where=filter
        )

    def update_by_document_id(
        self,
        document_id: uuid.UUID,
        document_name: str | None = None,
        valid_at: datetime | None = None,
        expired_at: datetime | None = None
    ):
        properties = {}
        if document_name is not None:
            properties["document_name"] = document_name
        if valid_at is not None:
            properties["valid_at"] = valid_at.replace(tzinfo=self.TIMEZONE)
        if expired_at is not None:
            properties["expired_at"] = expired_at.replace(tzinfo=self.TIMEZONE)
        if len(properties) == 0:
            return

        filter = Filter.by_property("document_id").equal(document_id)
        response = self.collection.query.fetch_objects(
            filters=filter,
            limit=10000
        )
        for item in response.objects:
            self.collection.data.update(
                uuid=item.uuid,
                properties=properties
            )
        
    def delete_all(self):
        filter = Filter.by_property("document_id").not_equal(uuid.uuid4())
        self.collection.data.delete_many(
            where=filter
        )
        
    def close(self):
        self.client.close()



