import logging
from api.schema import *
from google import genai
from agent.config import get_chat_config
from agent.context import AgentContext

_logger = logging.getLogger(__name__)

class AgentService:
    def __init__(self, agent):
        self.agent = agent
    
    def chat(
        self, 
        conversation_id: str,
        request: ChatRequest
    ) -> dict:
        chat_config = get_chat_config(conversation_id=conversation_id)
        context = AgentContext(
            conversation_id=conversation_id,
            student_code=request.student_code,
        )
        result = self.agent.invoke(
            {
                "messages" : [
                    {"role": "user", "content": request.message}
                ]
            },
            config=chat_config,
            context=context
        )
        last_message = result["messages"][-1]
        content = last_message.content
        answer = "".join(
            item.get("text", "")
            for item in content
            if isinstance(item, dict) and item.get("type") == "text"
        )

        data = {
            "answer" : answer,
            "related_document" : context.related_document,
            "tool_data" : self.get_tool_data(result["messages"])
        }
          
        return data
    
    @staticmethod
    def get_tool_data(
        messages: list
    ):
        result = []
        for message in messages:
            if message.type == "tool":
                try:
                    result.append({
                        "tool_name" : message.name,
                        "data" : message.artifact
                    })
                except Exception as e:
                    _logger.warning(e)
                    pass
        return result
        