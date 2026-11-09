from app.config import app_config
from agent.agent import build_agent
from service.agent import AgentService
from service.analyzer import AnalyzerService
from dependency.client import s3_storage

_agent_config = app_config.agent_config

agent = build_agent(model=_agent_config.agent_model)

agent_service = AgentService(agent=agent)
analyzer_service = AnalyzerService(
    s3=s3_storage,
    api_key=_agent_config.api_key,
    model=_agent_config.analyzer_model
)
