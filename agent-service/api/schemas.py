from pydantic import BaseModel

class PostChatRequest(BaseModel):
    message: str