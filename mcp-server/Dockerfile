FROM python:3.11-slim

WORKDIR /app

COPY mcp-server/requirements.txt requirements.txt

RUN pip install --no-cache-dir -r requirements.txt

COPY mcp-server/server.py mcp-server/server.py
COPY sample-project sample-project

EXPOSE 8000

CMD ["python", "mcp-server/server.py"]