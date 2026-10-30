from uuid import UUID
from datetime import datetime
from rag.storage.s3_storage import S3
from rag.database.weaviate_db import WeaviateDB
from rag.ingestion.enrichment import Enricher
from rag.ingestion.read_doc import read_doc
from rag.ingestion.chunking import create_chunk_by_size

class IngestionPipeline:
    def __init__(
        self,
        storage: S3,
        chunk_size: int,
        database: WeaviateDB,
        enricher: Enricher
    ):
        self.storage = storage
        self.chunk_size = chunk_size
        self.database = database
        self.enricher = enricher
        
    def run(
        self,
        file_id: UUID,
        file_name: str,
        file_type: str,
        from_cohort: int,
        to_cohort: int,
        keywords: list[str],
        valid_at: datetime,
        expired_at: datetime
    ) -> dict: 
        file_data = self.storage.get_file(file_id=file_id)
        doc = read_doc(data=file_data, file_type=file_type)
        chunks = create_chunk_by_size(doc=doc, size=self.chunk_size)
        chunk_index = 0
        for content in chunks:
            self.database.insert(
                file_id=file_id, 
                content=content, 
                chunk_index=chunk_index,
                from_cohort=from_cohort,
                to_cohort=to_cohort,
                keywords=keywords,
                valid_at=valid_at,
                expired_at=expired_at
            )
            chunk_index += 1
            