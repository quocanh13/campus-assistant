import uuid
from typing import Any
from pydantic import BaseModel
from datetime import datetime

class IngestDocumentRequest(BaseModel):
    document_id: uuid.UUID
    document_type: str
    document_name: str
    from_cohort: int
    to_cohort: int
    keywords: list[str]
    valid_at: datetime
    expired_at: datetime = datetime.fromisoformat("5000-01-01T00:00:00")
  
class RetrieveRequest(BaseModel):
    query: str
    keywords: list[str] = []
    cohort: int | None = None
    is_valid: bool = True
    document_id: uuid.UUID | None = None
    
class BaseResponse(BaseModel):
    code: str
    message: str | None = None
    detail: list[dict] | None = None
    data: Any = None
    
    @staticmethod
    def error(
        code: str,
        message: str,
        detail: list[dict] | None = None
    ) -> "BaseResponse":
        return BaseResponse(
            code=code,
            message=message,
            detail=detail
        )
    
    @staticmethod
    def success(
        message: str | None = None,
        data: Any = None
    ) -> "BaseResponse":
        return BaseResponse(
            code="SUCCESS", 
            message=message, 
            data=data
        )
    
        
    