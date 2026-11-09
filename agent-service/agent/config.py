def get_chat_config(conversation_id: str):
    return {
        "configurable" : {
            "thread_id" : conversation_id
        }
    }