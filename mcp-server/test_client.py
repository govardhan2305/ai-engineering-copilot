import asyncio

from mcp import ClientSession
from mcp.client.streamable_http import streamable_http_client


async def main():

    async with streamable_http_client(
        "http://127.0.0.1:8000/mcp"
    ) as streams:

        read_stream, write_stream, _ = streams

        async with ClientSession(
            read_stream,
            write_stream
        ) as session:

            await session.initialize()

            print("\n=== SEARCH CODE ===")

            result = await session.call_tool(
                "search_code",
                {
                    "query": "JWT"
                }
            )

            print(result)

            print("\n=== GET FILE ===")

            result = await session.call_tool(
                "get_file",
                {
                    "path": "authentication.md"
                }
            )

            print(result)

            print("\n=== SEARCH API ===")

            result = await session.call_tool(
                "search_api",
                {
                    "query": "Controller"
                }
            )

            print(result)


if __name__ == "__main__":
    asyncio.run(main())