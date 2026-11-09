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
    agent_model: str = os.getenv("AGENT_MODEL")
    analyzer_model: str = os.getenv("ANALYZER_MODEL")
    api_key: str = os.getenv("GOOGLE_API_KEY")

@dataclass(frozen=True)
class RagClientConfig:
    base_url: str = os.getenv("RAG_BASE_URL")

@dataclass(frozen=True)
class RedisConfig:
    host: str = os.getenv("REDIS_HOST")
    port: int = int(os.getenv("REDIS_PORT"))
    db: int = int(os.getenv("REDIS_DB"))
    
@dataclass(frozen=True)
class MySqlConfig:
    host: str = os.getenv("MYSQL_HOST")
    port: int = int(os.getenv("MYSQL_PORT"))
    user: str = os.getenv("MYSQL_USER")
    password: str = os.getenv("MYSQL_PASSWORD")
    database: str = os.getenv("MYSQL_DATABASE")
    pool_name: str = _config_dict["mysql"]["pool_name"]
    pool_size: int = _config_dict["mysql"]["pool_size"]

@dataclass(frozen=True)
class S3Config:
    url: str = os.getenv("S3_URL")
    bucket: str = _config_dict["s3"]["bucket"]
    root_user: str = os.getenv("S3_ROOT_USER")
    root_password: str = os.getenv("S3_ROOT_PASSWORD")

@dataclass(frozen=True)
class DebugConfig:
    num_hyphen: int = int(_config_dict["debug"]["num_hyphen"])

@dataclass(frozen=True)
class AppConfig:
    logging_config = LoggingConfig()
    fast_api_config = FastApiConfig()
    agent_config = AgentConfig()
    rag_client_config = RagClientConfig()
    redis_config = RedisConfig()
    mysql_config = MySqlConfig()
    s3_config = S3Config()
    debug_confg = DebugConfig()
    
app_config = AppConfig()

    