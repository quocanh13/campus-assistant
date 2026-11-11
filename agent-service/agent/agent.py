from langchain.agents import create_agent
from tool.rag import *
from tool.major import *
from tool.document import *
from tool.student import *
from langgraph.checkpoint.memory import InMemorySaver
from .context import AgentContext

SYSTEM_PROMPT="""
    Bạn là trợ lý hỗ trợ việc hỏi đáp, tra cứu tài liệu về các quy định, quy chế, chính sách, thủ tục, ... của đại học.
    Hãy sử dụng các công cụ được cung cấp để tra cứu tài liệu và trả lời người dùng.
    
    Khi người dùng hỏi về một chủ đề gì đó thì hãy dùng tool find_documents để tìm liệu có tài liệu nào liên quan trước không. Khi gọi find_documents, truyền từ khóa tìm kiếm vào name và description để tận dụng tìm kiếm full-text trên cả tên và mô tả tài liệu. Nếu có thì gọi rag_search chỉ trên tài liệu đó để tránh bị loãng thông tin và tốn thời gian. Còn nếu không có thì gọi rag_search luôn để tìm.
    
    Ưu tiên sử dụng các tài liệu mới nhất trước, chỉ dùng các tài liệu cũ nếu tài liệu mới không đủ thông tin để trả lời.
    
    Khi người dùng hỏi về điều kiện tiếng anh để có thể được làm gì đó (đăng ký học phần, giới hạn tín chỉ, nhận đồ án, ...), trước hết hãy kiếm tra điều kiện để được làm việc đó trước rồi từ đó mới xem xét tới điều kiện ngoại ngữ thay vì tìm điều kiện ngoại ngữ trước vì điều kiện ngoại ngữ có thể xuất hiện trước đó nhưng đã có văn bản mới liên quan đến việc đó thay đổi điều kiện rồi.
"""


def build_agent(model: str):
    memory = InMemorySaver()
    
    return create_agent(
        model=model,
        tools=[
            rag_search, 
            get_chunk_by_indexes,
            get_marjor_infomation,
            find_documents,
            find_major_by_name,
            get_student_information
        ],
        system_prompt=SYSTEM_PROMPT,
        checkpointer=memory,
        context_schema=AgentContext
    )