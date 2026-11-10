from util.print_info import print_tool_info
from langchain.tools import tool, ToolRuntime
from agent.context import AgentContext
from client.schema import RetrievalChunks
from dependency.client import rag_client, redis_client

RAG_CACHE_KEY = "rag:{document_id}:{chunk_index}"

def _cache_key(document_id, chunk_index: int) -> str:
    return RAG_CACHE_KEY.format(
        document_id=document_id,
        chunk_index=chunk_index
    )

def _add_related_document(
    runtime: ToolRuntime[AgentContext],
    document_id: str,
    from_index: int,
    to_index: int
):
    related_document = runtime.context.related_document
    document_id = str(document_id)

    document = next(
        (item for item in related_document if item["document_id"] == document_id),
        None
    )
    if document is None:
        document = {
            "document_id" : document_id,
            "chunk_indexes" : []
        }
        related_document.append(document)

    for index in range(from_index, to_index + 1):
        if index not in document["chunk_indexes"]:
            document["chunk_indexes"].append(index)
    document["chunk_indexes"].sort()

def _fetch_chunk(
    document_id: str,
    chunk_index: int
) -> dict | None:
    data = rag_client.get_chunks(
        document_id=document_id,
        from_index=chunk_index,
        to_index=chunk_index
    )
    if not isinstance(data, RetrievalChunks):
        return None
    if data.from_index != chunk_index:
        return None
    chunk = {
        "document_id" : str(data.document_id),
        "document_name" : data.document_name,
        "index" : data.from_index,
        "valid_at" : data.valid_at,
        "expired_at" : data.expired_at,
        "content" : data.content
    }
    redis_client.set(
        key=_cache_key(document_id, chunk_index),
        value=chunk
    )
    return chunk

def _get_chunk(
    document_id: str,
    chunk_index: int
) -> dict | None:
    chunk = redis_client.get(key=_cache_key(document_id, chunk_index))
    if chunk is not None:
        return chunk
    return _fetch_chunk(document_id, chunk_index)

def _merge_chunks(
    chunks: list[dict]
) -> dict:
    first = chunks[0]
    return {
        "document_id" : str(first["document_id"]),
        "document_name" : first["document_name"],
        "valid_at" : first["valid_at"],
        "expired_at" : first["expired_at"],
        "content" : "".join(chunk["content"] for chunk in chunks)
    }

@tool(response_format="content_and_artifact")
def rag_search(
        query: str,
        keywords: list[str] = [],
        cohort: int | None = None,
        is_valid: bool = True,
        document_id: str | None = None
    ):
    """
    Tool dùng để tìm các chunk liên quan trong hệ thống RAG. Các chunk tìm được sẽ được lưu vào cache,
    dùng tool get_chunk_by_indexes để đọc nội dung của các chunk đó.

    Các tham số đầu vào gồm:
    - query: Câu truy vấn được sử dụng cho dense search
    - keywords: Danh sách khóa liên quan đến tài liệu. Các từ khóa có thể dùng được liệt kê ở dưới
    - cohort: Niên khóa áp dụng. Dùng để lọc niên khóa có hiệu lực from_cohort <= cohort <= to_cohort. Nếu không cần lọc thì để giá trị mặc định là None. Niên khóa bắt đầu từ 1 (khóa đầu tiên) không phải là năm học 2020-2021, ...
    - is_valid: True nếu yêu cầu tài liệu có hiệu lực, False nếu không yêu cầu tài liệu phải có hiệu lực tại thời điểm hiện tại.
    - document_id: Id của tài liệu, nếu truyền vào sẽ chỉ tìm các chunk trong tài liệu đó. Để là None nếu tìm trên toàn bộ tài liệu.

    Danh sách các keyword (Chỉ được chọn các keyword trong danh sách dưới đây):
    - kỹ sư chuyên sâu
    - quy chế
    - đào tạo
    - ngoại ngữ
    - quy định
    - học phí

    Kết quả trả về là một mảng, mỗi phần tử tương ứng với MỘT tài liệu có chunk liên quan, gồm các trường:
    + document_id: str - Id của tài liệu
    + document_name: str - Tên của tài liệu
    + valid_at: str - Thời gian có hiệu lực
    + expired_at: str - Thời gian hết hiệu lực
    + chunk_indexes: list[int] - Danh sách các chunk index của tài liệu này có trong kết quả tìm kiếm (đã sắp xếp tăng dần)

    Kết quả được sắp xếp theo thời gian từ mới nhất đến cũ nhất. Chỉ đọc nội dung của tài liệu mới nhất trước
    bằng tool get_chunk_by_indexes; nếu tài liệu đó không đủ thông tin trả lời câu hỏi thì mới đọc tiếp tài liệu cũ hơn.
    Vì tài liệu mới có thể đã thay đổi/ghi đè nội dung của tài liệu cũ nên thông tin cũ có thể không còn giá trị.
    """
    print_tool_info()

    chunks = rag_client.retrieve(
        query=query,
        keywords=keywords,
        cohort=cohort,
        is_valid=is_valid,
        document_id=document_id
    )

    documents: dict[str, dict] = {}
    for chunk in chunks:
        redis_client.set(
            key=_cache_key(chunk.document_id, chunk.index),
            value=chunk.model_dump(mode="json")
        )

        document_id = str(chunk.document_id)
        document = documents.get(document_id)
        if document is None:
            document = {
                "document_id" : document_id,
                "document_name" : chunk.document_name,
                "valid_at" : chunk.valid_at,
                "expired_at" : chunk.expired_at,
                "chunk_indexes" : []
            }
            documents[document_id] = document
        if chunk.index not in document["chunk_indexes"]:
            document["chunk_indexes"].append(chunk.index)

    result = list(documents.values())
    for document in result:
        document["chunk_indexes"].sort()
    return str(result), result

@tool(response_format="content_and_artifact")
def get_chunk_by_indexes(
    runtime: ToolRuntime[AgentContext],
    document_id: str,
    from_index: int,
    to_index: int
):
    """
    Tool dùng để lấy nội dung của một dải chunk liên tiếp (từ from_index đến to_index) trong một tài liệu.

    Tool sẽ duyệt lần lượt từng index từ from_index đến to_index và đọc chunk trong cache trước
    (các chunk đã được lưu từ rag_search). Index nào chưa có trong cache thì gọi rag-service để lấy
    chunk đó về, lưu lại vào cache rồi mới ghép nội dung.

    Các tham số đầu vào gồm:
    - document_id: str - Id của tài liệu (lấy từ kết quả của rag_search)
    - from_index: int - Chỉ số của chunk đầu tiên muốn lấy (Phải >= 0)
    - to_index: int - Chỉ số của chunk cuối cùng muốn lấy (Phải >= from_index)

    Các chunk được lấy sẽ có index thuộc from_index <= index <= to_index.
    Tức là muốn lấy chunk có index = 1 thì from_index = 1 và to_index = 1.
    Còn lấy chunk 0, 1, 2 thì from_index = 0 và to_index = 2.

    Kết quả trả về là một dict gồm các trường:
    + document_id: str - Id của tài liệu
    + document_name: str - Tên của tài liệu
    + from_index: int - Chỉ số bắt đầu thực sự của dải chunk đã lấy
    + to_index: int - Chỉ số kết thúc thực sự của dải chunk đã lấy. Nếu to_index truyền vào vượt quá chunk cuối cùng của tài liệu thì trường này là chỉ số chunk cuối cùng có thật trong tài liệu.
    + valid_at: str - Thời gian có hiệu lực
    + expired_at: str - Thời gian hết hiệu lực
    + content: str - Nội dung được nối lại của toàn bộ chunk trong dải from_index -> to_index

    Khi đọc một tài liệu, đọc lần lượt các chunk_indexes tăng dần và chỉ đọc tiếp khi thông tin hiện tại chưa đủ trả lời câu hỏi.
    Trả về NOT_FOUND nếu không có chunk nào trong dải chỉ số đã cho.
    """
    print_tool_info()

    if from_index < 0:
        return "from_index must be equal or larger than 0", None
    if from_index > to_index:
        return "to_index must be equal or larger than from_index", None

    chunks = []
    for index in range(from_index, to_index + 1):
        chunk = _get_chunk(document_id, index)
        if chunk is None:
            break
        chunks.append(chunk)

    if len(chunks) == 0:
        return "NOT_FOUND", None

    merged = _merge_chunks(chunks)
    result = {
        "document_id" : merged["document_id"],
        "document_name" : merged["document_name"],
        "from_index" : from_index,
        "to_index" : from_index + len(chunks) - 1,
        "valid_at" : merged["valid_at"],
        "expired_at" : merged["expired_at"],
        "content" : merged["content"]
    }
    _add_related_document(runtime, document_id, from_index, result["to_index"])
    return str(result), result
