### Project Structure

```java
spring-boot-config-security/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── tutorials/
│   │   │           └── springbootconfigsecurity/
│   │   │               ├── config/
│   │   │               │   ├── ApplicationConfig.java
│   │   │               │   ├── SecurityConfiguration.java
│   │   │               │   └── OpenApiConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── AuthenticationController.java
│   │   │               │   └── DemoController.java
│   │   │               ├── dto/
│   │   │               │   ├── AuthenticationRequest.java
│   │   │               │   ├── AuthenticationResponse.java
│   │   │               │   ├── RegisterRequest.java
│   │   │               │   └── RefreshTokenRequest.java
│   │   │               ├── entity/
│   │   │               │   ├── Role.java
│   │   │               │   └── User.java
│   │   │               ├── filter/
│   │   │               │   └── JwtAuthenticationFilter.java
│   │   │               ├── repository/
│   │   │               │   └── UserRepository.java
│   │   │               ├── service/
│   │   │               │   ├── AuthenticationService.java
│   │   │               │   ├── JwtService.java
│   │   │               │   └── TokenBlacklistService.java
│   │   │               └── SpringBootConfigSecurityApplication.java
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── org/
│               └── tutorials/
│                   └── springbootconfigsecurity/
│                       ├── controller/
│                       │   ├── AuthenticationControllerTest.java
│                       │   └── DemoControllerTest.java
│                       ├── service/
│                       │   └── AuthenticationServiceTest.java
│                       └── SpringBootConfigSecurityApplicationTests.java
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### DB Diagram PlantUML:
```java
@startuml
!define RECTANGLE class

actor User
participant "AuthenticationController" as AC
participant "AuthenticationService" as AS
participant "UserRepository" as UR
participant "RoleRepository" as RR
participant "JwtService" as JS
participant "UserDetailsService" as UDS
participant "PasswordEncoder" as PE
participant "AuthenticationManager" as AM
participant "SecurityFilterChain" as SFC
participant "JwtAuthenticationFilter" as JAF
participant "SecuredResource" as SR
database "Database" as DB

title Full Flow of Spring Boot Security Project

== User Registration ==
User -> AC: POST /api/v1/auth/register
AC -> AS: register(RegisterRequest)
AS -> UR: findByEmail(email)
UR --> AS: User (if exists)
AS -> PE: encode(password)
AS -> UR: save(new User)
UR -> DB: Save user
DB --> UR: User saved
AS -> JS: generateToken(user)
JS --> AS: JWT token
AS -> JS: generateRefreshToken(user)
JS --> AS: Refresh token
AS --> AC: AuthenticationResponse
AC --> User: 200 OK (AuthenticationResponse)

== User Login ==
User -> AC: POST /api/v1/auth/login
AC -> AS: authenticate(AuthenticationRequest)
AS -> AM: authenticate(email, password)
AM -> UDS: loadUserByUsername(email)
UDS -> UR: findByEmail(email)
UR --> UDS: User
UDS --> AM: UserDetails
AM -> PE: matches(rawPassword, encodedPassword)
PE --> AM: boolean (password match)
AM --> AS: Authentication object
AS -> JS: generateToken(user)
JS --> AS: JWT token
AS -> JS: generateRefreshToken(user)
JS --> AS: Refresh token
AS --> AC: AuthenticationResponse
AC --> User: 200 OK (AuthenticationResponse)

== Token Refresh ==
User -> AC: POST /api/v1/auth/refresh
AC -> AS: refreshToken(RefreshTokenRequest)
AS -> JS: validateRefreshToken(token)
JS --> AS: boolean (token valid)
AS -> JS: extractUsername(token)
JS --> AS: username
AS -> UR: findByEmail(username)
UR --> AS: User
AS -> JS: generateToken(user)
JS --> AS: New JWT token
AS --> AC: New AuthenticationResponse
AC --> User: 200 OK (New AuthenticationResponse)

== User Logout ==
User -> AC: POST /api/v1/auth/logout
AC -> AS: logout(token)
AS -> JS: invalidateToken(token)
JS -> DB: Invalidate token
DB --> JS: Token invalidated
AS --> AC: Logout successful
AC --> User: 200 OK "Logged out successfully"

== Accessing Secured Resource ==
User -> SFC: Request with JWT
SFC -> JAF: doFilterInternal(request, response, filterChain)
JAF -> JS: extractUsername(token)
JS --> JAF: username
JAF -> UDS: loadUserByUsername(username)
UDS -> UR: findByEmail(username)
UR --> UDS: User
UDS --> JAF: UserDetails
JAF -> JS: isTokenValid(token, userDetails)
JS --> JAF: boolean (token valid)
JAF -> SFC: Set Authentication in SecurityContext
SFC -> SR: Process request
SR --> User: Protected resource or 403 Forbidden

@enduml
```