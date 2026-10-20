from langchain.agents import create_agent
from tools.rag_tools import search_documents
from langgraph.checkpoint.memory import InMemorySaver

def build_agent(model: str):
    memory = InMemorySaver()
    
    return create_agent(
        model=model,
        tools=[search_documents],
        system_prompt=(
            "Bạn là trợ lý hỏi đáp tài liệu. "
            "Hãy sử dụng search_documents khi cần tra cứu. "
            "Trả lời dựa trên kết quả tìm kiếm."
        ),
        checkpointer=memory
    )