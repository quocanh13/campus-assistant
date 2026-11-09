import google.genai.errors as model_errors
import logging
from http import HTTPStatus
from fastapi import Request
from .schemas import BaseResponse
from .exception import BusinessException
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException
from resources import exception_messages

_logger = logging.getLogger(__name__)

async def business_exception_handler(
    request: Request,
    exception: BusinessException
):
    message = exception_messages["business"][exception.code]["message"]
    status_code = exception_messages["business"][exception.code]["status_code"]
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
        code=HTTPStatus(exception.status_code).name,
        message=exception.detail,      
    )
    return JSONResponse(
        status_code=exception.status_code,
        content=response_data.model_dump(exclude_none=True)
    )

async def model_exception_handler(
    request: Request,
    exception: model_errors.ServerError | model_errors.ClientError
) : 
    response_data = BaseResponse.error(
        code=exception.status,
        message=exception.message
    )
    return JSONResponse(
        status_code=exception.code,
        content=response_data.model_dump(exclude_none=True)
    )
    
async def global_exception_handler(
    request: Request,
    exception: Exception
) : 
    _logger.error(exception)
    message = exception_messages["server"]["SERVER_ERROR"]["message"]
    status_code = exception_messages["server"]["SERVER_ERROR"]["status_code"]
    response_data = BaseResponse.error(
        code="SERVER_ERROR",
        message=message,
        
    )
    return JSONResponse(
        status_code=status_code,
        content=response_data.model_dump(exclude_none=True)
    )