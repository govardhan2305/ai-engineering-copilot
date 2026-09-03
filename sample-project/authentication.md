# Authentication

The application uses JWT-based authentication.

JwtFilter intercepts incoming HTTP requests and validates the JWT token.

SecurityConfig configures Spring Security and registers JwtFilter.

AuthService is responsible for authentication and token generation.

UserController exposes authenticated user APIs.
