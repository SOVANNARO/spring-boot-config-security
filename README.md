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
!define TABLE(name,desc) class name as "desc" << (T,#FFAAAA) >>
!define PK(x) <u>x</u>
!define FK(x) <i>x</i>

TABLE(User, "User\n(User table)") {
    PK(id) : Integer
    firstname : String
    lastname : String
    email : String
    password : String
    role : Enum
}
@enduml
```

### workflow diagram using PlantUML
```java
@startuml
actor Client
participant AuthenticationController
participant AuthenticationService
participant UserRepository
participant JwtService
participant AuthenticationManager
participant JwtAuthenticationFilter
participant SecurityContext
participant ProtectedResource

== User Registration ==
Client -> AuthenticationController : POST /api/v1/auth/register
AuthenticationController -> AuthenticationService : register()
AuthenticationService -> UserRepository : save(User)
AuthenticationService -> JwtService : generateToken()
AuthenticationController --> Client : Return Token

== User Authentication ==
Client -> AuthenticationController : POST /api/v1/auth/authenticate
AuthenticationController -> AuthenticationService : authenticate()
AuthenticationService -> AuthenticationManager : authenticate()
AuthenticationService -> JwtService : generateToken()
AuthenticationController --> Client : Return Token

== Accessing Protected Resources ==
Client -> JwtAuthenticationFilter : Request with JWT in Header
JwtAuthenticationFilter -> JwtService : validateToken()
JwtAuthenticationFilter -> SecurityContext : setAuthentication()
JwtAuthenticationFilter -> ProtectedResource : forward request
ProtectedResource --> Client : Response

== Token Refresh (if implemented) ==
Client -> AuthenticationController : POST /api/v1/auth/refresh
AuthenticationController -> AuthenticationService : refreshToken()
AuthenticationService -> JwtService : validateRefreshToken()
AuthenticationService -> JwtService : generateNewAccessToken()
AuthenticationController --> Client : Return New Access Token

== Logout (if implemented) ==
Client -> AuthenticationController : POST /api/v1/auth/logout
AuthenticationController -> AuthenticationService : logout()
AuthenticationService -> "TokenBlacklist" : addToBlacklist()
AuthenticationController --> Client : Logout Confirmation

@enduml
```