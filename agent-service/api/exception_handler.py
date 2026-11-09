import logging
from fastapi import Request
from .schema import BaseResponse
from .exception import BusinessException
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException
from api.resources.resource import get_exception_state
from google.genai.errors import ServerError, ClientError

_logger = logging.getLogger(__name__)

async def business_exception_handler(
    request: Request,
    exception: BusinessException
):
    key = f"business.{exception.code}"
    message, status_code = get_exception_state(key)
    response_data = BaseResponse.error(
        code=exception.code,
        message=message,
    )

    return JSONResponse(
        status_code=status_code,
        content=response_data.model_dump(exclude_none=True)
    )

async def validation_exception_handler(
    request: Request,
    exception: RequestValidationError
):
    errors = exception.errors()
    detail = []
    for error in errors:
        detail.append({
            "field" : ".".join(error["loc"]),
            "message" : error["msg"]
        })
    response_data = BaseResponse.error(
        code="INVALID_DATA",
        message="Invalid data",
        detail=detail
    )

    return JSONResponse(
        status_code=422,
        content=response_data.model_dump(exclude_none=True)
    )
    
async def http_exception_handler(
    request: Request,
    exception: HTTPException
) : 
    response_data = BaseResponse.error(
        code="RESOURCE_NOT_FOUND",
        message=exception.detail,      
    )
    return JSONResponse(
        status_code=404,
        content=response_data.model_dump(exclude_none=True)
    )

async def model_exception_handler(
    request: Request,
    exception: ServerError | ClientError
) : 
    response_data = BaseResponse.error(
        code=exception.status,
        message=exception.message
    )
    
    if(exception.code == 503):
        response_data.message = "Agent is currently experiencing high demand. Spikes in demand are usually temporary. Please try again later."
    
    return JSONResponse(
        status_code=exception.code,
        content=response_data.model_dump(exclude_none=True)
    )
    
async def global_exception_handler(
    request: Request,
    exception: Exception
) : 
    _logger.error(exception)
    key = f"server.SERVER_ERROR"
    message, status_code = get_exception_state(key)
    response_data = BaseResponse.error(
        code="SERVER_ERROR",
        message=message,
        
    )
    return JSONResponse(
        status_code=status_code,
        content=response_data.model_dump(exclude_none=True)
    )