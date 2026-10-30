from .schemas import *
from fastapi import APIRouter, Request
from .schemas import BaseResponse
from rag import *

router = APIRouter()

@router.post("/ingest")
async def ingest_document(request: IngestDocumentRequest):
    ingestion_pipeline.run(
        file_id=request.file_id, 
        file_type=request.file_type,
        file_name=request.file_name,
        from_cohort=request.from_cohort,
        to_cohort=request.to_cohort,
        keywords=request.keywords,
        valid_at=request.valid_at,
        expired_at=request.expired_at
    )
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)

@router.delete("/documents/{id}")
async def delete_document(id: uuid.UUID):
    weaviate_db.delete_by_file_id(id)
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)

@router.post("/retrieve")
async def retrieve(
    request: RetrieveRequest, 
):
    result = retrieval_pipeline.retrieve(
        query=request.query,
        keywords=request.keywords,
        cohort=request.cohort,
        is_valid=request.is_valid
    )
    response = BaseResponse.success(data=result)
    return response.model_dump(exclude_none=True)