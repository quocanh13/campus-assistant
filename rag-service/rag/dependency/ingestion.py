from app.config import app_config
from rag.ingestion.enrichment import Enricher

ingestion_config = app_config.ingestion_config

enricher = Enricher(
    model=ingestion_config.enricher_model,
    api_key=ingestion_config.enricher_api_key,
    max_try=ingestion_config.enrich_max_try
)