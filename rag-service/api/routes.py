from .schemas import *
from fastapi import APIRouter
from .schemas import BaseResponse
from rag import *

router = APIRouter()

@router.post("/ingest")
async def ingest_document(request: IngestDocumentRequest):
    metadata = ingestion_pipeline.run(
        file_id=request.file_id, 
        file_type=request.file_type,
        file_name=request.file_name
    )
    response = BaseResponse.success(    
        data=metadata
    )
    return response.model_dump(exclude_none=True)

@router.post("/generate")
async def generate(request: QueryRequest):
    result = generation_pipeline.generate(query=request.query)
    response = BaseResponse.success(
        data=result
    )
    return response.model_dump(exclude_none=True)

@router.delete("/documents/{id}")
async def delete_document(id: uuid.UUID):
    weaviate_db.delete_by_file_id(id)
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)

@router.post("/retrieve")
async def retrieve(request: RetrieveRequest):
    result = retrieval_pipeline.retrieve(query=request.query)
    response = BaseResponse.success(data=result)
    return response.model_dump(exclude_none=True)