from uuid import UUID
from pydantic import BaseModel
from typing import Any
from datetime import datetime

class ChatRequest(BaseModel):
    message: str
    
class ChatResponse(BaseModel):
    answer: str
    rag_data: list[dict]

class BaseResponse(BaseModel):
    code: str
    message: str | None
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