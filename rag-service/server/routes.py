from .schemas import *
from fastapi import APIRouter
from .schemas import BaseResponse
from rag import ingestion_pipeline, generation_pipeline, weaviate_db

router = APIRouter()

@router.post("/ingestion")
async def ingest_document(request: IngestDocumentRequest):
    metadata = ingestion_pipeline.run(
        file_id=request.file_id, 
        file_type=request.file_type,
        file_name=request.file_name
    )
    response = BaseResponse.success(
        message="Ingest file successfully",
        data=metadata
    )
    return response.model_dump(exclude_none=True)

@router.post("/query")
async def ingest_document(request: QueryRequest):
    result = generation_pipeline.generate(query=request.query)
    response = BaseResponse.success(
        message="Query successfully",
        data=result
    )
    return response.model_dump(exclude_none=True)

@router.delete("/documents/{id}")
async def delete_document(id: uuid.UUID):
    weaviate_db.delete_by_file_id(id)
    response = BaseResponse.success(
        message="Delete document successfully"
    )
    return response.model_dump(exclude_none=True)