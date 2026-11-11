import jwt
from app.config import app_config
from jwt.exceptions import InvalidTokenError

_jwt_config = app_config.jwt_config

def verify_jwt(token: str) -> dict:
    try:
        payload = jwt.decode(
            token,
            _jwt_config.secret_key,
            algorithms=[_jwt_config.algorithm]
        )
        return payload

    except InvalidTokenError:
        raise ValueError("Invalid or expired JWT")