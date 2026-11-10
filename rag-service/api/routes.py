from .schemas import *
from fastapi import APIRouter, Request, HTTPException
from .schemas import BaseResponse
from rag import *

router = APIRouter()

@router.post("/ingest")
def ingest_document(request: IngestDocumentRequest):
    ingestion_pipeline.run(
        document_id=request.document_id, 
        document_type=request.document_type,
        document_name=request.document_name,
        from_cohort=request.from_cohort,
        to_cohort=request.to_cohort,
        keywords=request.keywords,
        valid_at=request.valid_at,
        expired_at=request.expired_at
    )
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)

@router.delete("/documents/{id}")
def delete_document(id: uuid.UUID):
    weaviate_db.delete_by_document_id(id)
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)

@router.patch("/documents/{id}")
def update_document(id: uuid.UUID, request: UpdateDocumentRequest):
    weaviate_db.update_by_document_id(
        document_id=id,
        document_name=request.document_name,
        valid_at=request.valid_at,
        expired_at=request.expired_at
    )
    response = BaseResponse.success()
    return response.model_dump(exclude_none=True)


@router.get("/chunks")
def get_chunk(
    document_id: uuid.UUID,
    from_index: int,
    to_index: int
):
    if(from_index < 0):
        raise HTTPException(
            status_code=400,
            detail="from_index must be equal or larger than 0"
        )
    if(from_index > to_index):
        raise HTTPException(
            status_code=400,
            detail="to_index must be equal or larger than from_index"
        )
    result = weaviate_db.find_by_chunk_index(
        document_id=document_id,
        index=(from_index, to_index)
    )
    if(len(result) == 0):
        raise HTTPException(
            status_code=404,
            detail="No chunk was found"
        )

    data = [
        {
            "document_id" : record.properties["document_id"],
            "document_name" : record.properties["document_name"],
            "index" : record.properties["chunk_index"],
            "valid_at" : record.properties["valid_at"],
            "expired_at" : record.properties["expired_at"],
            "content" : record.properties["content"]
        }
        for record in result
    ]

    response = BaseResponse.success(data=data)
    return response.model_dump(exclude_none=True)

@router.post("/retrieve")
def retrieve(
    request: RetrieveRequest, 
):
    result = retrieval_pipeline.retrieve(
        query=request.query,
        keywords=request.keywords,
        cohort=request.cohort,
        is_valid=request.is_valid,
        document_id=request.document_id
    )
    response = BaseResponse.success(data=result)
    return response.model_dump(exclude_none=True)