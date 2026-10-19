import logging
from config import app_config
from rag.storage.s3_storage import S3

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

s3_config = app_config.s3_config
s3 = S3(
    url=s3_config.url,
    root_user=s3_config.root_user,
    root_password=s3_config.root_password,
    bucket=s3_config.bucket
)
try:
    s3.client.head_bucket(Bucket=s3_config.bucket)
    logger.info("S3 storage exists")
except:
    s3.client.create_bucket(
        Bucket=s3_config.bucket
    )
    logger.info("Init s3 successfully")


