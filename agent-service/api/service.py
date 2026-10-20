from api.schemas import *
from agent.config import get_chat_config

class ChatService:
    def __init__(self, agent):
        self.agent = agent
    
    def chat(
        self, 
        chat_id: str,
        request: PostChatRequest
    ) -> PostChatResponse:
        chat_config = get_chat_config(chat_id=chat_id)
        result = self.agent.invoke(
            {
                "messages" : [
                    {"role": "user", "content": request.message}
                ]
            },
            config=chat_config
        )
        
        last_message = result["messages"][-1]
        content = last_message.content
        answer = "".join(
            item.get("text", "")
            for item in content
            if isinstance(item, dict) and item.get("type") == "text"
        )

        data = PostChatResponse(
            answer=answer
        )
         
        return data