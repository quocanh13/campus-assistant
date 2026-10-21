import json
import logging
from datetime import datetime
from google import genai
from api.exception import BusinessException, BusinessExceptionCode

_SYSTEM_INSTRUCTION="""
Hãy đọc file được đính kèm, dựa trên tên file và nội dung trả về metadata của file đó cho tôi.
Đầu vào gồm có file đính kèm và nội dung đoạn chat dạng JSON gồm có tên file, ví dụ:
{
    "name" : "Quy chế đào tạo"
}
Metadata có dạng JSON với các trường như sau:
- document_type (string): Loại tài liệu. Là một xâu string chỉ được là 1 trong 4 giá trị sau: 
    + "regulation": Tài liệu về các quy định, quy chế điều kiện, quyền/nghĩa vụ, tiêu chí mà sinh viên/cán bộ phải tuân thủ. Ví dụ quy chế đào tạo, quy định về chuẩn tiếng anh,...
    + "policy": Các chính sách của nhà trường. Ví dụ chính sách hỗ trợ sinh viên nghèo, chính sách học bổng, ...
    + "procedure": Các văn bản về thủ tục, quy trình để thực hiện gì đó. Ví dụ quy trình đăng ký lớp học, quy trình đăng ký nhận học bổng, ...
    + "other": Các loại văn bản khác 3 loại trên
- cohort (int): Khóa bắt đầu áp dụng. Ví dụ quy định tiếng anh dành cho khóa 68. Nếu áp dụng cho toàn bộ các khóa thì giá trị là 0. Để khi tìm kiếm thì tôi sẽ lọc >= cohort.
- keyword (string[]): Một mảng các key word liên quan tới tài liệu. Các key word chỉ được là các từ sau:
    + "tuition" : Có liên quan đến học phí
    + "academic" : Có liên quan đến học tập (Cách tính điểm, điều kiện môn học, ...)
    + "foreign language" : Có liên quan đến điều kiện ngoại ngữ 
    + "scholarship" : Có liên quan đến học bổng
    + "graduation" : Có liên quan đến việc tốt nghiệp
    
Chỉ trả về JSON không có thêm gì khác. Ví dụ:
{
    "document_type" : "regulation",
    "cohort" : 68,
    "keyword" : ["academic", "foreign language"]
}
Không được thêm dấu mark down ''' ở đầu và cuối JSON
"""
_logger = logging.getLogger(__name__)

class Enricher:
    def __init__(
        self,
        model: str,
        api_key: str,
        max_try: int
    ):
        self._model = model
        self._client = genai.Client(api_key=api_key)
        self._max_try = max_try
        
    def enrich(
        self,
        name: str,
        mime_type: str,
        data: bytes
    ) -> dict:
        response = self._client.models.generate_content(
            model=self._model,
            config=genai.types.GenerateContentConfig(
                system_instruction=_SYSTEM_INSTRUCTION,
                response_mime_type="application/json"
            ),
            contents=[
                genai.types.Part.from_bytes(
                    data=data,
                    mime_type=mime_type
                ),
                f"""
                    "name" : {name}
                """
            ]
        )
        
        for _ in range(0, self._max_try):
            try:
                metadata = json.loads(response.text)
                metadata["keyword"] = " ".join(metadata["keyword"])
                return metadata
            except:
                _logger.warning("Invalid JSON format")
                print(metadata)

        raise BusinessException(code="INGESTION_FAILED")
        
        
