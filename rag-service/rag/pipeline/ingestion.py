from uuid import UUID
from datetime import datetime
from rag.storage.s3_storage import S3
from rag.database import DocumentWeaviate, ConversationWeaviate
from rag.ingestion.enrichment import Enricher
from rag.ingestion.read_doc import read_doc
from rag.ingestion.chunking import create_chunk_by_size
from api.schemas import ConversationMessage

class IngestDocumentPipeline:
    def __init__(
        self,
        storage: S3,
        chunk_size: int,
        document_weaviate: DocumentWeaviate,
        enricher: Enricher
    ):
        self.storage = storage
        self.chunk_size = chunk_size
        self.document_weaviate = document_weaviate
        self.enricher = enricher
        
    def run(
        self,
        document_id: UUID,
        document_name: str,
        document_type: str,
        from_cohort: int,
        to_cohort: int,
        keywords: list[str],
        valid_at: datetime,
        expired_at: datetime
    ) -> dict: 
        document_data = self.storage.get_file(document_id=document_id)
        doc = read_doc(data=document_data, file_type=document_type)
        chunks = create_chunk_by_size(doc=doc, size=self.chunk_size)
        chunk_index = 0
        for content in chunks:
            self.document_weaviate.insert(
                document_id=document_id, 
                document_name=document_name,
                content=content, 
                chunk_index=chunk_index,
                from_cohort=from_cohort,
                to_cohort=to_cohort,
                keywords=keywords,
                valid_at=valid_at,
                expired_at=expired_at
            )
            chunk_index += 1

class IngestConversationPipeline:
    def __init__(
        self,
        conversation_weaviate: ConversationWeaviate,
    ):
        self.conversation_weaviate = conversation_weaviate
        
    def run(
        self,
        conversation_id: UUID,
        title: str,
        messages: list[ConversationMessage]
    ):
        roles = []
        contents = []
        for message in messages:
            roles.append(f"{message.role}")
            contents.append(f"{message.content}")
        self.conversation_weaviate.insert(
            conversation_id=conversation_id,
            title=title,
            contents=contents,
            roles=roles
        )
        