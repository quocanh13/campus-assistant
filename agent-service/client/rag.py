import requests
from uuid import UUID
from client.schema import BaseResponse, RetrievedDocumentChunk, RetrievedConversationChunk

class DocumentRagClient:
    def __init__(
        self,
        base_url: str
    ):
        self._base_url = base_url
        
    def retrieve(
        self,
        query: str,
        keywords: list[str] = [],
        cohort: int | None = None,
        is_valid: bool = True,
        document_id: UUID | str | None = None
    ) -> list[RetrievedDocumentChunk]:
        url = f"{self._base_url}/document/retrieve"
        request_data = {
            "query" : query,
            "keywords": keywords,
            "cohort" : cohort,
            "is_valid" : is_valid,
            "document_id": str(document_id) if document_id is not None else None
        }
        response = requests.post(
            url=url,
            json=request_data
        )
        response_data = BaseResponse[list[RetrievedDocumentChunk]].model_validate(response.json())
        return response_data.data

    def get_chunks(
        self,
        document_id: UUID | str,
        from_index: int,
        to_index: int
    ) -> list[RetrievedDocumentChunk] | None:
        url = f"{self._base_url}/document/chunks"
        response = requests.get(
            url=url,
            params={
                "document_id": str(document_id),
                "from_index": from_index,
                "to_index": to_index
            }
        )
        if response.status_code >= 300:
            return None
        response_data = BaseResponse[list[RetrievedDocumentChunk]].model_validate(response.json())
        return response_data.data

class ConversationRagClient:
    def __init__(
        self,
        base_url: str
    ):
        self.base_url = base_url
    
    def retrieve(
        self,
        query: str
    ):
        url = f"{self.base_url}/conversation/retrieve"
        request_data = {
            "query" : query
        }
        response = requests.post(
            url=url,
            json=request_data
        )
        response_data = BaseResponse[list[RetrievedConversationChunk]].model_validate(response.json())
        return response_data.data