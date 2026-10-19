import fitz
import logging
from io import BytesIO
from pathlib import Path
from docx import Document
from typing import Callable

_logger = logging.getLogger(__name__)


def read_docx(input: Path | bytes) -> str:
    if isinstance(input, Path):
        doc = Document(input)
    else:
        doc = Document(BytesIO(input))
    result = []
    for para in doc.paragraphs:
        result.append(para.text)
    return "\n".join(result)

def read_pdf(input: Path | bytes) -> str:
    if(isinstance(input, Path)):
        doc = fitz.open(input)
    else:
        doc = fitz.open(stream=input, filetype="pdf")
    result = []
    for page in doc:
        result.append(page.get_text())
    doc.close()
    return "\n".join(result)

def read_txt(source: Path | bytes) -> str:
    if isinstance(source, Path):
        return source.read_text(encoding="utf-8")

    return source.decode("utf-8")

_FILE_TYPE_MAP: dict[str, Callable] = {
    # Microsoft Word
    ".docx": read_docx,
    ".doc": read_docx,
    "docx": read_docx,
    "doc": read_docx,
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document": read_docx,
    "application/msword": read_docx,

    # PDF
    ".pdf": read_pdf,
    "pdf" : read_pdf,
    "application/pdf": read_pdf,

    # Plain text
    ".txt": read_txt,
    "txt" : read_txt,
    "text/plain": read_txt
}

def read_doc(
    path: Path | str | None = None,
    data: bytes | None = None,
    file_type: str | None = None
):
    if path is not None:
        return read_from_path(path=path)
    if data is not None and file_type is not None:
        return read_from_bytes(data=data, file_type=file_type)
    else:
        raise ValueError("""Invalid arguments, path or both data and file_type must not be None""")

def read_from_path(path: Path | str) -> str:
    path = Path(path)
    suffix = path.suffix
    read_func = _FILE_TYPE_MAP.get(suffix, None)
    if(read_func == None):
        _logger.warning(f"Inalid file type : {suffix} -- Automatically read as .txt file")
        read_func = read_txt
    return read_func(path)

def read_from_bytes(
    data: bytes, 
    file_type: str
):
    read_func = _FILE_TYPE_MAP.get(file_type, None)
    if(read_func == None):
        _logger.warning(f"Inalid file type : {file_type} -- Automatically read as .txt file")
        read_func = read_txt
    return read_func(data)