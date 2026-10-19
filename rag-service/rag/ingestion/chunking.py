def create_chunk_by_size(
    doc: str,
    size: int
) -> list[str]:    
    doc_len = len(doc)
    res = []
    for i in range(0, doc_len - size, size):
        res.append(doc[i: i + size])
    res.append(doc[-size : ])
    return res
