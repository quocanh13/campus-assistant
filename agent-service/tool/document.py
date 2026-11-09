import uuid
from util.print_info import print_tool_info
from langchain.tools import tool
from dependency.client import mysql_pool
from mysql.connector.connection import MySQLConnection

@tool(response_format="content_and_artifact")
def get_document_list(
        name: str,
        limit: int = 10
    ) -> str:
    """
    Tool dùng để tìm kiếm tài liệu dựa trên tên
    Các tham số đầu vào gồm:
    + name: str - Tên của tài liệu. Tên không cần khớp 100% để tìm kiếm được vì đây là tìm kiếm dựa trên độ tương đồng của xâu.
    + limit: int - Giới hạn số lượng tài liệu lấy về để tránh quá nhiều, mặc định là 10

    Kết quả trả về một mảng chứa thông tin về tài liệu với các trường như sau:
    + id: str - Id của tài liệu 
    + name: str - Tên của tài liệu
    + valid_at: str - Thời gian có hiệu lực của tài liệu
    + expired_at: str - Thời gian hết hạn của tài liệu
    + score: float - Mức độ tương đồng của name, score càng cao thì name càng giống
    
    Lưu ý: Thời gian đều ở định dạng ISO 8601 : YYYY-MM-DDTHH:mm:ss
    """
    print_tool_info()
    
    connection: MySQLConnection = mysql_pool.get_connection()
    try:
        with connection.cursor(dictionary=True) as cursor:
            cursor.execute(
                """
                    SELECT id, name, valid_at, expired_at,
                    MATCH(name) AGAINST(%s IN NATURAL LANGUAGE MODE) AS score
                    FROM documents
                    ORDER BY score DESC, valid_at DESC
                    LIMIT %s;
                """, (name, limit)
            )
            rows = cursor.fetchall()
            for row in rows:
                row["id"] = str(uuid.UUID(bytes=row["id"]))
            return str(rows), rows
    finally:
        connection.close()


