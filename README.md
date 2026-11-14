# Book Catalog Service

A lightweight Spring Boot REST service demonstrating auto-configuration, configuration properties binding, profiles, and Actuator health endpoints.

## Starters Used
1. **`spring-boot-starter-web`**: Provides full web support, an embedded **Apache Tomcat** server, MVC architecture, and **Jackson** for automatic Java-to-JSON serialization.
2. **`spring-boot-starter-actuator`**: Exposes production-ready operational endpoints such as `/actuator/health`.

## How to Build and Run

### Build Executable JAR
```bash
mvn clean package