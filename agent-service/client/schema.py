import uuid
from typing import Any
from pydantic import BaseModel
from typing import Any, Generic, TypeVar

class RetrieveRequest:
    query: str
    keywords: list[str] = []
    cohort: int | None = None
    is_valid: bool = True

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
    