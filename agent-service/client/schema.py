import uuid
from typing import Generic, TypeVar
from pydantic import BaseModel

class RetrievedChunk(BaseModel):
    document_id: uuid.UUID
    document_name: str
    index: int
    valid_at: str
    expired_at: str
    content: str

class RetrievalChunks(BaseModel):
    document_id: uuid.UUID
    document_name: str
    from_index: int
    to_index: int
    valid_at: str
    expired_at: str
    content: str

T = TypeVar("T")
class BaseResponse(BaseModel, Generic[T]):
    code: str
    message: str | None = None
    detail: list[dict] | None = None
    data: T = None
