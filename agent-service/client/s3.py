import uuid
import boto3

class S3:
    def __init__(
        self,
        url: str,
        root_user: str,
        root_password: str,
        bucket: str = "rag"
    ):
        self.client = boto3.client(
            "s3",
            endpoint_url=url,
            aws_access_key_id=root_user,
            aws_secret_access_key=root_password
        )
        self.bucket = bucket
    
    def get_file(self, document_id: uuid.UUID) -> bytes:
        response = self.client.get_object(
            Bucket=self.bucket,
            Key=str(document_id)
        )

        data = response["Body"].read()
        return data
        
        
    