from app.config import app_config
from client.rag import RagClient
from client.redis import RedisClient
from client.mysql_db import create_pool
from client.s3 import S3
from app.config import app_config

_rag_client_config = app_config.rag_client_config
_redis_config = app_config.redis_config
_mysql_config = app_config.mysql_config
_s3_config = app_config.s3_config

rag_client = RagClient(_rag_client_config.base_url)

redis_client = RedisClient(
    host=_redis_config.host,
    port=_redis_config.port,
    db=_redis_config.db
)

mysql_pool = create_pool(
    host=_mysql_config.host,
    port=_mysql_config.port,
    user=_mysql_config.user,
    password=_mysql_config.password,
    database=_mysql_config.database,
    pool_name=_mysql_config.pool_name,
    pool_size=_mysql_config.pool_size
)

s3_storage = S3(
    url=_s3_config.url,
    bucket=_s3_config.bucket,
    root_user=_s3_config.root_user,
    root_password=_s3_config.root_password
)