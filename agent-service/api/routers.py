from fastapi import APIRouter
from api.schemas import *
from api.dependency import *

router = APIRouter()

@router.post("/chat/{chat_id}")
def post_chat(chat_id: str, request: PostChatRequest):
    data = chat_service.chat(
        chat_id=chat_id,
        request=request
    )
    response = BaseResponse.success(
        data=data
    )
    return response.model_dump(exclude_none=True)
    