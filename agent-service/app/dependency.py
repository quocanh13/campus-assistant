from app.config import app_config
from agents.agent import build_agent

agent_config = app_config.agent_config

agent = build_agent(model=agent_config.model)

