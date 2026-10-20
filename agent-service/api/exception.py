from typing import Literal

BusinessExceptionCode = Literal[""]
    
class BusinessException(Exception):
    def __init__(
        self, 
        code: BusinessExceptionCode,
    ):
        super().__init__()
        self.code: BusinessExceptionCode = code
        