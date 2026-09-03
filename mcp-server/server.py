from pathlib import Path

from mcp.server.fastmcp import FastMCP


PROJECT_ROOT = Path(__file__).resolve().parent.parent / "sample-project"

mcp = FastMCP(
    "AI Engineering Copilot",
    host="0.0.0.0",
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
def get_file(path: str) -> str:
    """
    Read the contents of a specific project file.

    Use the exact relative path returned by search_code()
    or list_project_files().
    """

    print(f"[MCP] get_file called with path: {path}", flush=True)

    file_path = (PROJECT_ROOT / path).resolve()

    if not file_path.is_relative_to(PROJECT_ROOT.resolve()):
        return "Error: Access outside the project directory is not allowed."

    if not file_path.exists():
        return f"Error: File not found: {path}"

    if not file_path.is_file():
        return f"Error: Path is not a file: {path}"

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


    return search_code(query)


if __name__ == "__main__":
    mcp.run(transport="streamable-http")