import logging
from fastapi import FastAPI
from app.config import app_config
from app.routes import router
from app.exception_handler import *

_logger = logging.getLogger(__name__)
fast_api_config = app_config.fast_api_config

app = FastAPI(
    title=fast_api_config.title,
    description=fast_api_config.description,
    version=fast_api_config.version
)

app.include_router(router)

app.add_exception_handler(BusinessException, business_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(HTTPException, http_exception_handler)
app.add_exception_handler(model_errors.ServerError, validation_exception_handler)
app.add_exception_handler(model_errors.ClientError, validation_exception_handler)
app.add_exception_handler(Exception, global_exception_handler)

_logger.info("Created app")        
