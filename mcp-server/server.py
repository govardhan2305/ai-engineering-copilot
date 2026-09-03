from pathlib import Path

from mcp.server.fastmcp import FastMCP


PROJECT_ROOT = Path(__file__).resolve().parent.parent / "sample-project"

mcp = FastMCP(
    "AI Engineering Copilot",
    host="127.0.0.1",
    port=8000
)


@mcp.tool()
def list_project_files() -> list[str]:
    """List all files available in the sample project."""
    print(f"[MCP] search_code called with query: {query}", flush=True)

    files = []

    for path in PROJECT_ROOT.rglob("*"):
        if path.is_file():
            files.append(str(path.relative_to(PROJECT_ROOT)))

    return sorted(files)


@mcp.tool()
def get_file(query: str) -> str:
    """
    Read a specific project file.

    IMPORTANT:
    - This tool accepts exactly one argument named "path".
    - Do NOT use "query" with this tool.
    - The path must be the relative file path returned by list_project_files
      or search_code.
    """
    print(f"[MCP] search_code called with query: {query}", flush=True)

    file_path = (PROJECT_ROOT / query).resolve()

    if not file_path.is_relative_to(PROJECT_ROOT.resolve()):
        return "Error: Access outside the project directory is not allowed."

    if not file_path.exists():
        return f"Error: File not found: {query}"

    if not file_path.is_file():
        return f"Error: Path is not a file: {query}"

    return file_path.read_text(encoding="utf-8")

@mcp.tool()
def read_jwt_filter() -> str:
    """
    Read the JwtFilter source code from the sample project.

    Use this tool when the user asks about JWT token validation
    or the JwtFilter implementation.
    """

    file_path = (
        PROJECT_ROOT
        / "src"
        / "main"
        / "java"
        / "com"
        / "example"
        / "demo"
        / "security"
        / "JwtFilter.java"
    )

    print("[MCP] read_jwt_filter called", flush=True)

    if not file_path.exists():
        return "Error: JwtFilter.java was not found."

    return file_path.read_text(encoding="utf-8")


@mcp.tool()
def search_code(query: str) -> list[dict[str, str]]:
    """
    Search project source files for a text query.

    IMPORTANT:
    - This tool accepts exactly one argument named "query".
    - It returns matching file paths and source lines.
    - Use get_file(path) after finding the desired file.
    """
    print(f"[MCP] search_code called with query: {query}", flush=True)

    results = []

    for path in PROJECT_ROOT.rglob("*"):

        if not path.is_file():
            continue

        try:
            content = path.read_text(encoding="utf-8")
        except (UnicodeDecodeError, PermissionError):
            continue

        for line_number, line in enumerate(content.splitlines(), start=1):

            if query.lower() in line.lower():

                results.append({
                    "file": str(path.relative_to(PROJECT_ROOT)),
                    "line": str(line_number),
                    "content": line.strip()
                })

    return results


@mcp.tool()
def search_api(query: str) -> list[dict[str, str]]:
    """Search project files for API-related definitions."""
    print(f"[MCP] search_code called with query: {query}", flush=True)

    return search_code(query)


if __name__ == "__main__":
    mcp.run(transport="streamable-http")