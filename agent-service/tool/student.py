import json

from langchain.tools import tool, ToolRuntime
from mysql.connector import MySQLConnection

from agent.context import AgentContext
from dependency.client import mysql_pool
from util.print_info import print_tool_info


def _find_student(code: int) -> dict | None:
    connection: MySQLConnection = mysql_pool.get_connection()

    try:
        query = """
            SELECT JSON_OBJECT(
                'code', s.id,
                'name', s.name,
                'cohort', s.cohort,
                'major', JSON_OBJECT(
                    'code', m.code,
                    'name', m.name,
                    'instruction_language', m.instruction_language,
                    'program_type', m.program_type,
                    'unit', JSON_OBJECT(
                        'code', u.code,
                        'name', u.name
                    )
                )
            ) AS student
            FROM students s
            JOIN majors m ON s.major = m.code
            JOIN units u ON m.unit_code = u.code
            WHERE s.id = %s;
        """
        with connection.cursor(dictionary=True) as cursor:
            cursor.execute(query, (code,))
            row = cursor.fetchone()

            if row is None:
                return None

            return json.loads(row["student"])

    finally:
        connection.close()


@tool(response_format="content_and_artifact")
def get_student_information(
    runtime: ToolRuntime[AgentContext]
):
    """
    Tool dùng để tra cứu thông tin của sinh viên đang trò chuyện.

    Tool không nhận tham số đầu vào. Mã sinh viên luôn được lấy từ context của
    cuộc trò chuyện (student_code), không cho phép agent truyền mã sinh viên vào.

    Kết quả trả về là một dict gồm các trường sau:
    - code: int - Mã sinh viên
    - name: str - Họ và tên sinh viên
    - cohort: int - Niên khóa (khóa thứ mấy, ví dụ 66)
    - major: dict - Thông tin ngành học của sinh viên, gồm:
        + code: str - Mã ngành học
        + name: str - Tên ngành học
        + instruction_language: str - Ngôn ngữ giảng dạy chính
        + program_type: str - Loại chương trình đào tạo (ví dụ chương trình chuẩn, tiên tiến, ...)
        + unit: dict - Thông tin đơn vị quản lý ngành, gồm:
            - code: str - Mã đơn vị
            - name: str - Tên đơn vị

    Lưu ý: Trả về None nếu không tìm thấy sinh viên có mã tương ứng.
    """
    print_tool_info()

    student_code = None
    if runtime is not None and runtime.context is not None:
        student_code = runtime.context.student_code

    if student_code is None:
        return "STUDENT_CODE_REQUIRED", None

    result = _find_student(student_code)

    if result is None:
        return None, None

    return str(result), result
