from fastapi import APIRouter
from api.schema import *
from dependency.agent import agent_service, analyzer_service

router = APIRouter()

@router.post("/conversation/{conversation_id}")
def chat(conversation_id: UUID, request: ChatRequest):
    result = agent_service.chat(
        conversation_id=conversation_id,
        request=request
    )
    response = BaseResponse.success(
        data=result
    )
    return response.model_dump(exclude_none=True)

@router.get("/documents/{document_id}/analyze")
def analyze_document(document_id: str):
    result = analyzer_service.analyze_document(document_id)
    response = BaseResponse.success(
        data=result
    )
    return response.model_dump(exclude_none=True)