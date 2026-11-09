import uuid
from util.print_info import print_tool_info
from langchain.tools import tool
from dependency.client import mysql_pool
from mysql.connector.connection import MySQLConnection


@tool(response_format="content_and_artifact")
def find_documents(
        name: str,
        description: str,
        limit: int = 3
    ) -> str:
    """
    Tool dùng để tìm kiếm tài liệu bằng full-text search trên tên và mô tả.

    Các tham số đầu vào gồm:
    + name: str - Từ khóa tìm kiếm trên tên tài liệu (trọng số 60%).
    + description: str - Từ khóa tìm kiếm trên mô tả tài liệu (trọng số 40%).
    + limit: int - Giới hạn số lượng tài liệu lấy về, mặc định là 3.

    Tên và mô tả không cần khớp 100% vì đây là tìm kiếm dựa trên độ tương đồng của từ khóa.

    Tool sẽ lấy ra `limit` tài liệu có điểm tương đồng cao nhất, sau đó sắp xếp lại
    các tài liệu đó theo valid_at giảm dần (tài liệu mới nhất trước).

    Kết quả trả về là một mảng chứa thông tin về tài liệu với các trường như sau:
    + id: str - Id của tài liệu
    + name: str - Tên của tài liệu
    + description: str - Mô tả của tài liệu
    + valid_at: str - Thời gian có hiệu lực của tài liệu
    + expired_at: str - Thời gian hết hạn của tài liệu

    Lưu ý: Thời gian đều ở định dạng ISO 8601 : YYYY-MM-DDTHH:mm:ss
    """
    print_tool_info()

    name = name.strip()
    description = description.strip()

    limit = max(1, limit)
    connection: MySQLConnection = mysql_pool.get_connection()
    try:
        if name and description:
            query = """
                SELECT id, name, description, valid_at, expired_at
                FROM (
                    SELECT id, name, description, valid_at, expired_at,
                    (0.6 * MATCH(name) AGAINST(%s IN NATURAL LANGUAGE MODE)
                    + 0.4 * MATCH(description) AGAINST(%s IN NATURAL LANGUAGE MODE)) AS score
                    FROM documents
                    HAVING score > 0
                    ORDER BY score DESC
                    LIMIT %s
                ) AS top_documents
                ORDER BY valid_at DESC;
            """
            params = (name, description, limit)

        with connection.cursor(dictionary=True) as cursor:
            cursor.execute(query, params)
            rows = cursor.fetchall()
            for row in rows:
                row["id"] = str(uuid.UUID(bytes=row["id"]))
                if row["valid_at"] is not None:
                    row["valid_at"] = row["valid_at"].isoformat()
                if row["expired_at"] is not None:
                    row["expired_at"] = row["expired_at"].isoformat()
            return str(rows), rows
    finally:
        connection.close()
