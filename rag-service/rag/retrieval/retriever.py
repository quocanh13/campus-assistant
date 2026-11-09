from uuid import UUID
from rag.database.weaviate_db import WeaviateDB

class Retriever:
    def __init__(
        self, 
        weaviate_db: WeaviateDB,
        adj_limit: int
    ):
        self._weaviate_db = weaviate_db
        self._adj_limit = adj_limit


    def retrieve(
        self, 
        chunks
    ) -> tuple[str, list[UUID]]:
    
        index_lists: dict[UUID, list[tuple[int, int]]] = {}
        for chunk in chunks:
            document_id = chunk.properties["document_id"]
            chunk_index = chunk.properties["chunk_index"]
            index_lists[document_id] = index_lists.get(document_id, [])
            index_lists[document_id].append((chunk_index - self._adj_limit, chunk_index + self._adj_limit))
            
        doc_contexts = []
        result = []
        for document_id, index_list in index_lists.items():
            index_list = self._filter_index(index_list)
            for start, end in index_list:
                chunks = self._weaviate_db.find_by_chunk_index(document_id, (start, end))
                contents = [chunk.properties["content"] for chunk in chunks]
                merged_content = "".join(contents)
                result.append({
                    "document_id" : document_id,
                    "document_name" : chunks[0].properties["document_name"],
                    "from_index" : start,
                    "to_index" : end,
                    "valid_at" : chunks[0].properties["valid_at"],
                    "expired_at" : chunks[0].properties["expired_at"],
                    "content" : merged_content
                })
        result = sorted(result, key=lambda x: x["valid_at"], reverse=True)
        return result
    
    def _get_doc_context(self, file_id: UUID, index_list: list[tuple[int, int]]):
            chunks = self._weaviate_db.find_by_chunk_index(file_id, index_list)
            contents = [chunk.properties["content"] for chunk in chunks]
            doc_context = "".join(contents)
            return(doc_context)
    
    @staticmethod
    def _filter_index(index_list: list[tuple[int, int]]) ->  list[tuple[int, int]]:
        index_list.sort(key=lambda x: x[0])
        new_index_list = []
        a = index_list[0][0]
        b = index_list[0][1]
        for c, d in index_list:
            if(b >= c):
                b = max(b, d)
            else:
                new_index_list.append((a, b))
                a = c
                b = d
        new_index_list.append((a, b))
        return new_index_list
                 