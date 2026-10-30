import logging
import argparse
import weaviate
from app.config import app_config
from rag.dependency.database import weaviate_db
from weaviate.classes.config import Configure, Property, DataType

parser = argparse.ArgumentParser()
parser.add_argument("--reset", action="store_true")
args = parser.parse_args()

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

def init_weaviate():
    weaviate_config = app_config.weaviate_config
    client: weaviate.WeaviateClient = weaviate.connect_to_weaviate_cloud(
        cluster_url=weaviate_config.url,
        auth_credentials=weaviate.auth.AuthApiKey(api_key=weaviate_config.api_key)
    )
    if client.collections.exists(weaviate_config.collection):
        logger.info("Weaviate exists")
        if(args.reset):
            client.collections.delete(weaviate_config.collection)
            logger.info(f"Delete weaviate collection : f{weaviate_config.collection}")
        else:
            return
    client.collections.create(
        name=weaviate_config.collection,
        vector_config=Configure.Vectors.text2vec_weaviate(
            name="content_vector",
            source_properties=["content"]
        ),
        properties=[
            Property(name="file_id", data_type=DataType.UUID),
            Property(name="content", data_type=DataType.TEXT),
            Property(name="chunk_index", data_type=DataType.INT),
            Property(name="from_cohort", data_type=DataType.INT),
            Property(name="to_cohort", data_type=DataType.INT),
            Property(name="keywords", data_type=DataType.TEXT_ARRAY),
            Property(name="valid_at", data_type=DataType.DATE),
            Property(name="expired_at", data_type=DataType.DATE)
        ]
    )
    logger.info("Init weaviate successfully")
    client.close()

def delete_all_document():
    weaviate_db.delete_all()

delete_all_document()
init_weaviate()

