from app.config import app_config
from ..storage.s3_storage import S3

_s3_config = app_config.s3_config

s3_storage = S3(
    url=_s3_config.url,
    bucket=_s3_config.bucket,
    root_user=_s3_config.root_user,
    root_password=_s3_config.root_password
)