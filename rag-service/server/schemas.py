import uuid
from typing import Any
from pydantic import BaseModel

class QueryRequest(BaseModel):
    query: str
    user_id: str
    
class IngestDocumentRequest(BaseModel):
    file_id: uuid.UUID
    file_type: str
    file_name: str

class QueryRequest(BaseModel):
    query: str

class BaseResponse(BaseModel):
    code: str
    message: str
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
        message: str,
        data: Any = None
    ) -> "BaseResponse":
        return BaseResponse(
            code="SUCCESS", 
            message=message, 
            data=data
        )
    
        
    