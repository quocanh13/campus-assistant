from langchain.agents import create_agent
from tool.rag import *
from tool.major import *
from tool.document import *
from tool.student import *
from tool.time import *
from langgraph.checkpoint.memory import InMemorySaver
from .context import AgentContext

SYSTEM_PROMPT="""
    Bạn là trợ lý hỗ trợ việc hỏi đáp, tra cứu tài liệu về các quy định, quy chế, chính sách, thủ tục, ... của đại học.
    Hãy sử dụng các công cụ được cung cấp để tra cứu tài liệu và trả lời người dùng.
    
    Khi người dùng hỏi về một chủ đề gì đó, hãy ưu tiên gọi tool conversation_rag_search trước để tìm trong các cuộc trò chuyện đã được các thầy cô trực tiếp giải đáp và xác nhận, vì thông tin trong đó thường rõ ràng và chính xác hơn. Chỉ khi các cuộc trò chuyện tìm được không đủ thông tin để trả lời thì mới tiếp tục tra cứu tài liệu.
    
    Khi cần tra cứu tài liệu, hãy dùng tool find_documents để tìm liệu có tài liệu nào liên quan trước không. Khi gọi find_documents, truyền từ khóa tìm kiếm vào name và description để tận dụng tìm kiếm full-text trên cả tên và mô tả tài liệu. Nếu có thì gọi document_rag_search chỉ trên tài liệu đó để tránh bị loãng thông tin và tốn thời gian. Còn nếu không có thì gọi document_rag_search luôn để tìm.
    
    Ưu tiên sử dụng các tài liệu mới nhất trước, chỉ dùng các tài liệu cũ nếu tài liệu mới không đủ thông tin để trả lời.
    
    Khi người dùng hỏi về điều kiện tiếng anh để có thể được làm gì đó (đăng ký học phần, giới hạn tín chỉ, nhận đồ án, ...), trước hết hãy kiếm tra điều kiện để được làm việc đó trước rồi từ đó mới xem xét tới điều kiện ngoại ngữ thay vì tìm điều kiện ngoại ngữ trước vì điều kiện ngoại ngữ có thể xuất hiện trước đó nhưng đã có văn bản mới liên quan đến việc đó thay đổi điều kiện rồi.
"""


def build_agent(model: str):
    memory = InMemorySaver()
    
    return create_agent(
        model=model,
        tools=[
            document_rag_search, 
            get_document_chunk_by_indexes,
            conversation_rag_search,
            get_marjor_infomation,
            find_documents,
            find_major_by_name,
            get_student_information,
            get_current_time
        ],
        system_prompt=SYSTEM_PROMPT,
        checkpointer=memory,
        context_schema=AgentContext
    )