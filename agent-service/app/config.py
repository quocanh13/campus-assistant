import os
import yaml
from dotenv import load_dotenv
from dataclasses import dataclass

load_dotenv()

_config_pth = f"./config.yaml"

with open(_config_pth) as file:
    _config_dict: dict = yaml.safe_load(file)    
    
@dataclass(frozen=True)
class LoggingConfig:
    level: str = _config_dict["logging"]["level"]
    format: str = _config_dict["logging"]["format"]
    filename: str = _config_dict["logging"]["filename"]    

@dataclass(frozen=True)
class FastApiConfig:
    title: str = _config_dict["fast_api"]["title"]
    description: str = _config_dict["fast_api"]["description"]
    version : str = _config_dict["fast_api"]["version"]
    host: str = os.getenv("API_HOST")
    port: int = int(os.getenv("API_PORT"))

@dataclass(frozen=True)
class AgentConfig:
    model: str = os.getenv("AGENT_MODEL")

@dataclass(frozen=True)
class AppConfig:
    logging_config = LoggingConfig()
    fast_api_config = FastApiConfig()
    agent_config = AgentConfig()
    
app_config = AppConfig()

    