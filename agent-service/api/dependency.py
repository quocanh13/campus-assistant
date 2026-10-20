from app.config import app_config
from agent.agent import build_agent
from api.service import ChatService

agent_config = app_config.agent_config

agent = build_agent(model=agent_config.model)
chat_service = ChatService(agent=agent)

