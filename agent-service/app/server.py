import logging
from fastapi import FastAPI
from app.config import app_config
from api.routers import router

_logger = logging.getLogger(__name__)
fast_api_config = app_config.fast_api_config

app = FastAPI(
    title=fast_api_config.title,
    description=fast_api_config.description,
    version=fast_api_config.version
)

app.include_router(router)

_logger.info("Created app")        
