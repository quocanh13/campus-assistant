from datetime import datetime

from langchain.tools import tool

from util.print_info import print_tool_info


@tool(response_format="content_and_artifact")
def get_current_time() -> str:
    """
    Tool dùng để lấy thời gian hiện tại của hệ thống.

    Tool không nhận tham số đầu vào.

    Kết quả trả về là một chuỗi thời gian theo định dạng ISO 8601 kèm múi giờ,
    ví dụ: 2026-11-11T15:30:00.123456+07:00
    """
    print_tool_info()
    time = datetime.now().astimezone().isoformat()
    return time, time
