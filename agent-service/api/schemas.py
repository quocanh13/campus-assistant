from pydantic import BaseModel
from typing import Any

class PostChatRequest(BaseModel):
    message: str
    
class PostChatResponse(BaseModel):
    answer: str
    
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