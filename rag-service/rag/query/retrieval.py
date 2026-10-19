from uuid import UUID
from rag.database.weaviate_db import WeaviateDB


class Retriever:
    def __init__(
        self, 
        weaviate_db: WeaviateDB,
        limit: int,
        adj_limit: int
    ):
        self._limit = limit
        self._weaviate_db = weaviate_db
        self._adj_limit = adj_limit


    def retrieve(self, processed_query: str) -> tuple[str, list[UUID]]:
        chunks = self._weaviate_db.dense_search(
            query=processed_query,
            limit=self._limit
        )
        index_lists: dict[UUID, list[tuple[int, int]]] = {}
        for chunk in chunks:
            file_id = chunk.properties["file_id"]
            chunk_index = chunk.properties["chunk_index"]
            index_lists[file_id] = index_lists.get(file_id, [])
            index_lists[file_id].append((chunk_index - self._adj_limit, chunk_index + self._adj_limit))
            
        related_file = []
        doc_contexts = []
        for file_id, index_list in index_lists.items():
            related_file.append(file_id)
            index_list = self._filter_index(index_list)
            doc_contexts.append(self._get_doc_context(file_id, index_list))
        context = "\n".join(doc_contexts)
        return context, related_file
    
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
                 