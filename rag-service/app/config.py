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
class DocumentWeaviateConfig:
    url: str = os.getenv("WEAVIATE_DOCUMENT_URL")
    api_key: str = os.getenv("WEAVIATE_DOCUMENT_API_KEY")
    collection: str = _config_dict["weaviate"]["document_collection"]

@dataclass(frozen=True)
class ConversationWeaviateConfig:
    url: str = os.getenv("WEAVIATE_CONVERSATION_URL")
    api_key: str = os.getenv("WEAVIATE_CONVERSATION_API_KEY")
    collection: str = _config_dict["weaviate"]["conversation_collection"]

@dataclass(frozen=True)
class S3Config:
    url: str = os.getenv("S3_URL")
    bucket: str = _config_dict["s3"]["bucket"]
    root_user: str = os.getenv("S3_ROOT_USER")
    root_password: str = os.getenv("S3_ROOT_PASSWORD")

@dataclass(frozen=True)
class IngestionConfig:
    chunk_size: int = _config_dict["ingestion"]["chunk_size"]
    enricher_model: str = _config_dict["ingestion"]["enricher_model"]
    enricher_api_key: str = os.getenv("ENRICHER_API_KEY")
    enrich_max_try: int = int(_config_dict["ingestion"]["enrich_max_try"])

@dataclass(frozen=True)
class QueryProcessingConfig:
    model: str = _config_dict["query_processing"]["model"]
    api_key: str = os.getenv("QUERY_PROCESSING_API_KEY")
    
@dataclass(frozen=True)
class RetrievalConfig:
    @dataclass(frozen=True)
    class Document:
        search_limit: int = int(_config_dict["retrieval"]["document"]["search_limit"])
        ranked_limit: int = int(_config_dict["retrieval"]["document"]["ranked_limit"])
        adj_limit: int = int(_config_dict["retrieval"]["document"]["adj_limit"])
        K: int = int(_config_dict["retrieval"]["document"]["K"])
        ratio: float = float(_config_dict["retrieval"]["document"]["ratio"])
    @dataclass(frozen=True)
    class Conversation:
        search_limit: int = int(_config_dict["retrieval"]["conversation"]["search_limit"])
        ranked_limit: int = int(_config_dict["retrieval"]["conversation"]["ranked_limit"])
        adj_limit: int = int(_config_dict["retrieval"]["conversation"]["adj_limit"])
        K: int = int(_config_dict["retrieval"]["conversation"]["K"])
        ratio: float = float(_config_dict["retrieval"]["conversation"]["ratio"])
    document = Document()
    conversation = Conversation()

@dataclass(frozen=True)
class GenerationConfig:
    model: str = _config_dict["generation"]["model"]
    api_key: str = os.getenv("GENERATION_API_KEY")

@dataclass(frozen=True)
class FastApiConfig:
    title: str = _config_dict["fast_api"]["title"]
    description: str = _config_dict["fast_api"]["description"]
    version : str = _config_dict["fast_api"]["version"]
    host: str = os.getenv("API_HOST")
    port: int = int(os.getenv("API_PORT"))

@dataclass(frozen=True)
class AppConfig:
    logging_config = LoggingConfig()
    document_weaviate_config = DocumentWeaviateConfig()
    conversation_weaviate_config = ConversationWeaviateConfig()
    s3_config = S3Config()
    ingestion_config = IngestionConfig()
    fast_api_config = FastApiConfig()
    query_processing_config = QueryProcessingConfig()
    retrieval_config = RetrievalConfig()
    generation_config = GenerationConfig()
    
app_config = AppConfig()

    