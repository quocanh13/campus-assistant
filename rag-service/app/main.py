import uvicorn
import logging
from app.config import app_config

def main():
    logging_config = app_config.logging_config
    fast_api_config = app_config.fast_api_config
    logging.basicConfig(
        level=logging_config.level,
        format=logging_config.format,
        filename=logging_config.filename
    )
    
    uvicorn.run(
        app="app.server:app",
        host=fast_api_config.host,
        port=fast_api_config.port,
        reload=True
    )


if __name__ == "__main__":
    main()