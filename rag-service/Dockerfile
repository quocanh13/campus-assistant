FROM python:3.11

WORKDIR "campus-assistant/rag-service"

COPY ./requirement.txt ./
COPY ./start.sh ./

RUN python -m pip install -r requirement.txt

EXPOSE 5000

CMD ["sh", "start.sh"]