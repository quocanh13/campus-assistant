import json
from dependency.client import mysql_pool
from util.print_info import print_tool_info
from langchain.tools import tool
from mysql.connector import MySQLConnection

@tool(response_format="content_and_artifact")
def get_marjor_infomation(
    code: str,
    include_unit: bool = False
):
    """
    Tool dùng để tra cứu thông tin về ngành học dựa trên mã ngành
    Tham số đầu vào gồm:
    code: str - Mã ngành chỉ gồm các ký tự viết tắt, dấu '-' và số, ví dụ như là IT1, ET1, ET2, IT-E10, BF-E12
    include_unit: bool - Lấy thêm thông tin về đơn vị mà ngành học đó thuộc về. Mặc định là False
    
    Kết quả trả về là một dict với các trướng như sau:
    name: str - Tên ngành học
    code: str - Mã ngành học
    instruction_language: str - Ngôn ngữ giảng dạy chính
    unit: dict - Thông tin về đơn vị. Gồm các trường sau:
        code: str - Mã đơn vị
        name: str - Tên đơn vị
    Lưu ý: Trả về None nếu không tìm thấy ngành nào có mã ngành tương ứng
    """
    print_tool_info()

    connection: MySQLConnection = mysql_pool.get_connection()

    try:
        if include_unit:
            query = """
                SELECT JSON_OBJECT(
                    'code', m.code,
                    'name', m.name,
                    'instruction_language', m.instruction_language,
                    'unit', CASE
                        WHEN u.code IS NULL THEN NULL
                        ELSE JSON_OBJECT(
                            'code', u.code,
                            'name', u.name
                        )
                    END
                ) AS major
                FROM majors m
                LEFT JOIN units u ON m.unit_code = u.code
                WHERE m.code = %s;
            """
        else:
            query = """
                SELECT JSON_OBJECT(
                    'code', code,
                    'name', name,
                    'instruction_language', instruction_language,
                    'unit_code', unit_code
                ) AS major
                FROM majors
                WHERE code = %s;
            """

        with connection.cursor(dictionary=True) as cursor:
            cursor.execute(query, (code,))
            row = cursor.fetchone()

            if row is None:
                return None, None

            result = json.loads(row["major"])

            return str(result), result

    finally:
        connection.close()

@tool(response_format="content_and_artifact")
def find_major_by_name(
    name: str
): 
    """
    Tool dùng để tra cứu mã ngành học dựa trên tên ngành học.
    Tham số đầu vào gồm:
    name: str - Tên ngành học cần tra cứu
    
    Kết quả trả về là một mảng với các phần tử có các trường như sau:
    name: str - Tên ngành học
    code: str - Mã ngành học
    score: float - Mức độ tương đồng giữa tên tìm kiếm và tên lưu trong cơ sở dữ liệu
    
    Lưu ý: Tool này chỉ dùng để tìm mã ngành. Nếu tìm được mã ngành rồi thì mà muốn lấy thông tin về ngành học thì dùng tool get_marjor_infomation
    """
    print_tool_info()

    connection: MySQLConnection = mysql_pool.get_connection()

    try:
        query = """
            SELECT code, name,
            MATCH(name) AGAINST(%s IN NATURAL LANGUAGE MODE) AS score
            FROM majors
            ORDER BY score DESC
            LIMIT 10;
        """
        with connection.cursor(dictionary=True) as cursor:
            cursor.execute(query, (name, ))
            rows = cursor.fetchall()
            return str(rows), rows

    finally:
        connection.close()