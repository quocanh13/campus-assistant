from google import genai
from google.genai import types
from uuid import UUID
from io import BytesIO
from client.s3 import S3

PROMPT = """
Bạn là một chuyên gia phân tích tài liệu.

Hãy đọc toàn bộ nội dung document được cung cấp và tạo
một bản mô tả ngắn gọn khoảng 400-500 từ bằng tiếng Việt.

Mục đích của bản mô tả là lưu vào MySQL Database
và sử dụng FULLTEXT INDEX để tìm kiếm tài liệu.

Yêu cầu:
1. Xác định chủ đề và mục đích chính của tài liệu.
2. Nêu các nội dung, kiến thức và thông tin quan trọng.
3. Liệt kê các thuật ngữ chuyên ngành, khái niệm,
   tên lĩnh vực và từ khóa quan trọng.
4. Giữ nguyên các tên riêng, thuật ngữ chuyên ngành
   và từ viết tắt cần thiết.
5. Sử dụng cách diễn đạt tự nhiên, đa dạng từ vựng
   nhưng không làm thay đổi ý nghĩa tài liệu.
6. Không suy diễn thông tin không xuất hiện trong file.
7. Không thêm lời mở đầu hoặc nhận xét cá nhân.
8. Chỉ trả về nội dung mô tả, không sử dụng Markdown.

Ưu tiên những từ khóa mà người dùng có thể sử dụng
khi tìm kiếm tài liệu bằng ngôn ngữ tự nhiên.
"""

class AnalyzerService:
    def __init__(
        self,
        api_key: str,
        model: str,
        s3: S3
    ):
        self.s3 = s3
        self.model = model
        self.gemini = genai.Client(api_key=api_key)

    def analyze_document(
        self,
        document_id: UUID
    ) -> str | None:

        uploaded_file = None
        try:
            document_data: bytes = self.s3.get_file(document_id)
            if not document_data:
                raise ValueError(f"Document {document_id} is empty")
            file_data = BytesIO(document_data)
            uploaded_file = self.gemini.files.upload(
                file=file_data,
                config=types.UploadFileConfig(
                    mime_type="application/pdf",
                    display_name="document.pdf"
                )
            )

            response = self.gemini.models.generate_content(
                model=self.model,
                contents=[uploaded_file, PROMPT]
            )
            return response.text.strip() if response.text else None

        except Exception as error:
            print(f"Error analyzing document: {error}")
            raise

        finally:
            if uploaded_file and uploaded_file.name:
                try:
                    self.gemini.files.delete(name=uploaded_file.name)
                except Exception as error:
                    print(f"Cannot delete uploaded file: {error}")