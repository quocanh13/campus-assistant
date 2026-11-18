import uuid
from typing import Generic, TypeVar
from pydantic import BaseModel

class RetrievedDocumentChunk(BaseModel):
    document_id: uuid.UUID
    document_name: str
    index: int
    valid_at: str
    expired_at: str
    content: str
    
class ConversationMessage(BaseModel):
    role: str
    content: str

class RetrievedConversationChunk(BaseModel):
    id: uuid.UUID
    conversation_id: uuid.UUID
    title: str
    messages: list[ConversationMessage]

T = TypeVar("T")
class BaseResponse(BaseModel, Generic[T]):
    code: str
    message: str | None = None
    detail: list[dict] | None = None
    data: T = None
