import json
import redis
import logging
from typing import Any

_logger = logging.getLogger(__name__)

class RedisClient:
    def __init__(
        self,
        host: str,
        port: int,
        db: int
    ):
        self.client = redis.Redis(
            host=host,
            port=port,
            db=db,
            decode_responses=True
        )
        
        try:
            if self.client.ping():
                _logger.info("Connected to Redis successfully!")

        except redis.ConnectionError as e:
            _logger.warning(f"Redis connection failed: {e}")
            
    def set(self, key: str, value: dict | str | list, ex: int = 600):
        if isinstance(value, dict) or isinstance(value, list):
            value = json.dumps(value, ensure_ascii=False)
        
        self.client.set(name=key, value=value, ex=ex)
    
    def get(self, key: str, default: Any = None) -> str | dict | list | None:
        raw = self.client.get(key)
        if(raw is None):
            return default
        try:
            value = json.loads(raw)
            return value
        except json.JSONDecodeError:
            return raw
        
    def append(self, key: str, value: dict | str):
        stored_value = self.get(key)
        if stored_value is None:
            stored_value = [value]
        else:
            if isinstance(stored_value, list):
                stored_value.append(value)
            else:
                raise RuntimeError("stored_value is not a list")
        self.set(key, stored_value)
        
    def delete(self, key: str):
        self.client.delete(key)
        
    def exists(self, key: str) -> bool:
        return bool(self.client.exists(key))

    def close(self):
        self.client.close()