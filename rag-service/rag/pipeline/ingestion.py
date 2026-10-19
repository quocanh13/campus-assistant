from uuid import UUID
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
        file_type: str
    ) -> dict: 
        file_data = self.storage.get_file(file_id=file_id)
        doc = read_doc(data=file_data, file_type=file_type)
        metadata = self.enricher.enrich(name=file_name, mime_type=file_type, data=file_data)
        chunks = create_chunk_by_size(doc=doc, size=self.chunk_size)
        chunk_index = 0
        for content in chunks:
            self.database.insert(
                file_id=file_id, 
                content=content, 
                chunk_index=chunk_index,
                document_type=metadata["document_type"],
                cohort=metadata["cohort"],
                keyword=metadata["keyword"]
            )
            chunk_index += 1
        return metadata
            