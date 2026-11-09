import uuid
from util.print_info import print_tool_info
from langchain.tools import tool, ToolRuntime
from agent.context import AgentContext
from dependency.client import rag_client, redis_client

@tool(response_format="content_and_artifact")
def rag_search(
        runtime: ToolRuntime[AgentContext],
        query: str,
        keywords: list[str] = [],
        cohort: int | None = None,
        is_valid: bool = True,
        document_id: str | None = None
    ):
    """
    Tool dùng để lấy các chunk được lưu hệ thống RAG về để thực hiện đọc các chunk đó sau 
    Các tham số đầu vào gồm:
    - query: Câu truy vấn được sử dụng cho dense search
    - keywords: Danh sách khóa liên quan đến tài liệu. Các từ khóa có thể dùng được liệt kê ở dưới
    - cohort: Niên khóa áp dụng. Dùng để lọc niên khóa có hiệu lực from_cohort <= cohort <= to_cohort. Nếu không cần lọc thì để giá trị mặc định là None. Niên khóa bắt đầu từ 1 (khóa đầu tiên) không phải là năm học 2020-2021, ...
    - is_valid: True nếu yêu cầu tài liệu có hiệu lực, False nếu không yêu cầu tài liệu phải có hiệu lực tại thời điểm hiện tại.
    - document_id: Id của tài lệu, nếu truyền vào sẽ chỉ tìm các chunk trong tài liệu đó. Để là None nếu tìm trên toàn bộ tài liệu.

    Danh sách các keyword (Chỉ được chọn các keyword trong danh sách dưới đây):
    - kỹ sư chuyên sâu
    - quy chế
    - đào tạo
    - ngoại ngữ
    - quy định
    - học phí

    Kết quả trả về là một dict với các trường như sau:
    rag_id: str - Id của kết quả truy vấn RAG hiện tại. Dùng để lấy các chunk trong kết quả truy vấn hiện tại theo thứ tự thời gian.
    num_chunk: int - Số lượng chunk có trong kết quả truy vấn hiện tại

    Kết quả trả về được sắp xếp theo thứ tự thời gian từ mới nhất đến cũ nhất. Giúp truy vấn tài liệu mới nhất trước nếu tài liệu mới mà không đủ thông tin trả lời câu hỏi thì mới gọi hàm get_next_chunk() để lấy tiếp các tài liệu cũ hơn. Vì nếu dùng tài liệu cũ trước để trả lời câu hỏi thì có thể xảy ra trường hợp tài liệu mới đã thay đổi một số phần và có hiệu lực ghi đè lên tài liệu cũ rồi nên những thông tin cũ đó giờ đã không còn giá trị, không đúng nữa.
    Vậy nên chỉ gọi get_next_chunk() để lấy tiếp thông tin chỉ khi mà thông tin hiện tại không đủ để trả lời câu hỏi
    """
    print_tool_info()
    
    response_data = rag_client.retrieve(
        query=query,
        keywords=keywords,
        cohort=cohort,
        is_valid=is_valid,
        document_id=document_id
    )
    rag_id = str(uuid.uuid4())
    num_chunk = len(response_data)
    result = {
        "rag_id" : rag_id,
        "num_chnk" : num_chunk
    }
    
    redis_client.append(
        key=f"rag:{runtime.context.conversation_id}",
        value=result
    )
    for i in range(num_chunk):
        redis_client.set(
            key=f"{rag_id}:{i}",
            value=response_data[i].model_dump(mode="json")
        )
    return str(result), result

@tool(response_format="content_and_artifact")
def get_next_chunks(
    rag_id: str,
    index: int,
    runtime: ToolRuntime[AgentContext]
):
    """
    Tool dùng để lấy các chunk (nội dung) của một lần truy vấn RAG (gọi tool rag_search)
    Các tham số đầu vào gồm:
    rag_id: str - Id của lần truy vấn RAG được trả về từ rag_search (Chỉ rag_id được trả về từ rag_search mới có tác dụng)
    index: int - Chỉ số của một nhóm chunks trong kết quả truy vấn. 
    
    Một nhóm chunks là các chunk thuộc cùng 1 document có chunk index sát nhau (ví dụ 5-6-7) được nối nội dung lại. Các nhóm chunk có thể thuộc cùng 1 document (nhưng chỉ số tách rời ví dụ 1-2-3 và 7-8-9) hoặc khác document
    
    Kết quả trả về là một dict với các trường như sau:
    + file_id: UUID - Id của document mà chunk/content này thuộc về,
    + file_name: str - Tên file mà trường này thuộc về,
    + from_index: int - Chỉ số bắt đầu của chunk trong file (Có thể được dùng để truy vấn thêm nội dung phía trước (dùng tool get_chunks_by_index) của doạn hiện tại nếu cần)
    + to_index: int - Chỉ số kết thúc của chunk trong file (Có thể được dùng để truy vấn thêm nội dung phía sau (dùng tool get_chunks_by_index) của doạn hiện tại nếu cần)
    + valid_at: str - Thời gian có hiệu lực
    + expired_at: str - Thời gian hết hiệu lực
    + content: str - Nội dụng được nối lại của các chunk từ from_index đến to_index
    Trả về None nếu không có chunk với index như đầu vào. (Hết chunk) 
    
    Bắt đầu gọi từ index = 0 trước và tăng dần chỉ khi lượng thông tin trước đó không đủ để trả lời câu hỏi.
    
    Các chunk (thông tin) được sắp xếp theo thứ tự thời gian từ mới tới cũ. Vậy nên không sử dụng các chunk cũ trước vì thông tin có thể đã lỗi thời. Chỉ gọi hàm để lấy các chunk với index lớn hơn khi mà hiện tại vẫn chưa đủ thông tin để trả lời câu hỏi.
    
    Trường hợp mà câu hỏi về các tài liệu cũ thì cũng phải gọi hàm để lấy thêm data
    """
    print_tool_info()
    result = redis_client.get(key=f"{rag_id}:{index}")
    return str(result), result

@tool(response_format="content_and_artifact")
def get_chunks_by_index(
    document_id: str,
    from_index: int,
    to_index: int
): 
    """
    Tool dùng để lấy chunk dựa trên document id và index. Sử dụng khi get_next_chunk() đã hết data lấy được nhưng vẫn cần thêm thông tin từ tài liệu đó.
    Các tham số đầu vào gồm:
    document_id: str - Id của document mà chunk thuộc về
    from_index: int - Chỉ số của chunk đầu tiên muốn lấy (Phải >= 0)
    to_index: int - Chỉ số của chunk cuối cùng muốn lấy (Phải >= from_index)
    
    Các chunk được lấy sẽ có index thuộc from_index <= index <= to_index. Tức là muốn lấy chunk có index = 1 thì from_index = 1 và to_index = 1. Còn lấy chunk thứ 0, 1, 2 thì from_chunk = 0, to_index = 2.
    
    Kết quả trả về là một dict có các trường như sau:
    + file_id: UUID - Id của document mà chunk/content này thuộc về,
    + file_name: str - Tên file mà trường này thuộc về,
    + from_index: int - Chỉ số bắt đầu của chunk trong file (Có thể được dùng để truy vấn thêm nội dung phía trước của doạn hiện tại nếu cần)
    + to_index: int - Chỉ số kết thúc của chunk trong file (Có thể được dùng để truy vấn thêm nội dung phía sau của doạn hiện tại nếu cần)
    + valid_at: str - Thời gian có hiệu lực
    + expired_at: str - Thời gian hết hiệu lực
    + content: str - Nội dụng được nối lại của các chunk từ from_index đến to_index
    
    Lưu ý: Có thể trả về NOT_FOUND nếu không tìm thấy document có id tương ứng hoặc index đã vượt quá index tối đa (Ví dụ chỉ có 10 chunk mà gọi hàm lấy chunk từ 11 đến 13)
    """
    print_tool_info()
    data = rag_client.get_chunks(document_id, from_index, to_index)
    return data.model_dump(mode="json"), data
    