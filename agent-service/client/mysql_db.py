from mysql.connector.pooling import MySQLConnectionPool
from mysql.connector.connection import MySQLConnection
from mysql.connector.cursor import MySQLCursorDict

def create_pool(
    pool_name: str,
    pool_size: int,
    host: str,
    port: int,
    user: str,
    password: str,
    database: str
):
    pool = MySQLConnectionPool(
        pool_name=pool_name,
        pool_size=pool_size,
        host=host,
        port=port,
        password=password,
        user=user,
        database=database
    ) 
    return pool      