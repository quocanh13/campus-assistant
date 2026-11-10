from dataclasses import dataclass, field

@dataclass
class AgentContext:
    conversation_id: str
    related_document: list[dict] = field(default_factory=list)
