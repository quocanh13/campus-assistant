import yaml

_EXCEPTION: dict[str, dict] = yaml.safe_load("./api/resources/exception.yaml")

def get_exception_state(key: str):
    return _EXCEPTION[key]