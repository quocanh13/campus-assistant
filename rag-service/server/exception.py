from typing import Literal

BusinessExceptionCode = Literal["INGESTION_FAILED"]
    
class BusinessException(Exception):
    def __init__(
        self, 
        code: BusinessExceptionCode,
    ):
        super().__init__()
        self.code = code


    @staticmethod
    def build(input: BusinessExceptionCode):
        if isinstance(input, BusinessExceptionCode):
            return BusinessException._build_from_business_exception_code(input)
        raise ValueError("Invalid input for BusinessException.build")
    
    @staticmethod
    def _build_from_business_exception_code(code: BusinessExceptionCode):
        return BusinessException(
            code=code
        )

    