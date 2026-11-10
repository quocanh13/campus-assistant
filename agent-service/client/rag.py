import requests
from uuid import UUID
from client.schema import BaseResponse, RetrievedChunk

class RagClient:
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
    ) -> list[RetrievedChunk]:
        url = f"{self._base_url}/retrieve"
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
        response_data = BaseResponse[list[RetrievedChunk]].model_validate(response.json())
        return response_data.data

    def get_chunks(
        self,
        document_id: UUID | str,
        from_index: int,
        to_index: int
    ) -> list[RetrievedChunk] | None:
        url = f"{self._base_url}/chunks"
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
        response_data = BaseResponse[list[RetrievedChunk]].model_validate(response.json())
        return response_data.data
