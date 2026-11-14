# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Compile
mvn compile

# Package
mvn package

# Run (default: email notification)
mvn exec:java -Dexec.mainClass=com.store.Main

# Run with SMS notification
mvn exec:java -Dexec.mainClass=com.store.Main -Dnotification.type=sms
```

No tests exist in this project yet.

## Architecture

This is a Spring Framework 6.1.5 learning project (Java 21, plain Spring — not Spring Boot) demonstrating core IoC/DI concepts.

**Entry point:** `Main.java` bootstraps Spring using `AnnotationConfigApplicationContext(AppConfig.class)`, retrieves `OrderService`, and calls `processOrder("order-1")`.

**Package structure:**
- `com.store.config` — Spring configuration and `Condition` classes
- `com.store.serivce` — Service interfaces (`OrderService`, `PaymentService`, `NotificationService`) — note the typo: `serivce` not `service`
- `com.store.serivce.impl` — Implementations

**Spring concepts in use:**

| Concept | Where |
|---|---|
| Java config (`@Configuration`, `@Bean`) | `AppConfig` registers payment beans and `StoreProperties` |
| Component scan | `@ComponentScan("com.store")` picks up `@Service`-annotated classes |
| Constructor injection | `OrderServiceImpl` — all dependencies injected via constructor; see the class-level comment for why this is preferred over field injection |
| `@Qualifier` | Selects `"card"` payment bean in `OrderServiceImpl` constructor |
| `ObjectFactory<T>` | Used in `OrderServiceImpl` to get a fresh `NotificationService` prototype on every `processOrder` call — the fix for the prototype-in-singleton scoping problem |
| Config bean | `StoreProperties` (registered in `AppConfig`) centralises store config instead of `@Value` fields scattered across services |
| `@Conditional` | `EmailNotificationService` and `SmsNotoficationService` activate based on the `notification.type` system property (default: `email`) |
| Prototype scope | Both notification services are `@Scope("prototype")`; `@PostConstruct` fires on each new instance, confirming fresh beans per request |
| Lifecycle hooks | `@PostConstruct` / `@PreDestroy` on notification services (note: Spring does **not** call `@PreDestroy` for prototype beans) |

**Key wiring detail:** `CardPaymentService` and `CashPaymentService` are **not** annotated with `@Service` — only registered through explicit `@Bean` methods in `AppConfig`. The `@Qualifier` on those classes is a custom qualifier marker for disambiguation.

**Scoping problem demo:** Run the app and observe the output. Three calls to `processOrder` on the same singleton print three different `NotificationService` instance IDs, each preceded by an `@PostConstruct` log — proof that `ObjectFactory` correctly defeats the prototype-in-singleton trap. Without `ObjectFactory` (plain setter/field injection), all three calls would print the same ID.

**Conditional notification selection:** `EmailCondition` and `SmsCondition` read `System.getProperty("notification.type", "email")` at context startup to decide which `NotificationService` bean to register.

## Branch Structure

The remote has topic branches for different learning modules:
`1_java_8_9_11`, `2_lambda_stream`, `3_effective_java`, `4_multithreading`, `5_db`, `spring_boot`
