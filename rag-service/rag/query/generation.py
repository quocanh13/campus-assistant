from google import genai
_SYSTEM_INSTRUCTION = """
Bạn là trợ lý hỏi đáp trong hệ thống RAG.

Quy tắc:
- Chỉ sử dụng thông tin có trong context.
- Không suy diễn hoặc bổ sung kiến thức bên ngoài.
- Nếu context không đủ để trả lời, hãy trả lời là không đủ thông tin để trả lời câu hỏi
- Trả lời ngắn gọn, chính xác và có trích dẫn nội dung từ context khi phù hợp.
"""

class Generator:
    def __init__(
        self,
        model: str,
        api_key: str
    ):
        self._model = model
        self._client = genai.Client(api_key=api_key)

    def generate_answer(self, query: str, context: str):
        contents = f"""
        Context:
        {context}

        Question:
        {query}

        Answer:
        """

        response = self._client.models.generate_content(
            model=self._model,
            config=genai.types.GenerateContentConfig(system_instruction=_SYSTEM_INSTRUCTION),
            contents=contents
        )
    
        return response.text
    
    
    