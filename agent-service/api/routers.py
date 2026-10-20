from fastapi import APIRouter
from api.schemas import PostChatRequest
from app.dependency import agent
from agents.config import get_chat_config

router = APIRouter()

@router.post("/chat/{chat_id}")
def post_chat(chat_id: str, request: PostChatRequest):
    chat_config = get_chat_config(chat_id=chat_id)
    result = agent.invoke(
        {
            "messages" : [
                {"role": "user", "content": request.message}
            ]
        },
        config=chat_config
    )
    return result["messages"][-1].content
    