# Sample Engineering Project

This sample project demonstrates a simple backend architecture.

## Authentication

The application uses JWT-based authentication.

Authentication-related components:

- `JwtFilter` validates incoming JWT bearer tokens.
- `SecurityConfig` configures the application's authentication flow.
- `UserController` exposes authenticated user APIs.

## User Management

`UserService` provides user lookup and authentication functionality.

## Important Components

### JwtFilter
Responsible for validating bearer tokens and extracting the username.

### SecurityConfig
Connects the security configuration with the JWT filter.

### UserController
Provides APIs for retrieving the authenticated user's information.
