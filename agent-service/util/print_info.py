import inspect
import sys
from app.config import app_config

MAX_NUM_HYPHEN = app_config.debug_confg.num_hyphen

def _safe_print(text: str):
    try:
        print(text)
    except UnicodeEncodeError:
        encoding = sys.stdout.encoding or "utf-8"
        print(text.encode(encoding, errors="replace").decode(encoding))

def print_tool_info():
    frame = inspect.currentframe().f_back

    function_name = frame.f_code.co_name
    arguments = inspect.getargvalues(frame)
    
    num_hypen = (MAX_NUM_HYPHEN - len(function_name)) // 2

    _safe_print("-" * num_hypen + function_name + "-" * num_hypen)

    for arg_name in arguments.args:
        if arg_name != "runtime":
            _safe_print(f"{arg_name}: {arguments.locals[arg_name]}")

    _safe_print("-" * MAX_NUM_HYPHEN + "\n")