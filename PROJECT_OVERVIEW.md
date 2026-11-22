# Akka Example Project Overview

This is a simple **Akka Typed (Java)** example demonstrating actor-to-actor communication using the classic (untyped) Akka Actor API. The project shows two actors (`ActorA` and `ActorB`) exchanging messages in a ping-pong pattern.

---

## 📁 Project Structure

```
akka-example/
├── build.gradle          # Gradle build configuration
├── build.sbt             # SBT build configuration
├── pom.xml               # Maven build configuration
├── gradlew / gradlew.bat # Gradle wrapper scripts
├── sbt / sbt.bat         # SBT wrapper scripts
├── .gitignore
├── src/
│   └── main/
│       ├── java/com/example/
│       │   ├── Main.java        # Entry point
│       │   ├── ActorA.java      # First actor
│       │   ├── ActorB.java      # Second actor
│       │   ├── MessageA.java    # String message type
│       │   └── MessageB.java    # Integer message type
│       └── resources/
│           └── logback.xml      # Logging configuration
└── sbt-dist/                    # SBT distribution files
```

---

## 🎯 How It Works

### Message Flow
```
Main → ActorA (MessageA "Starting")
       ↓
ActorA creates ActorB → ActorB (MessageA "Hello!")
       ↓
ActorB replies → ActorA (MessageB 42)
       ↓
ActorA replies → ActorB (MessageB 999)
       ↓
ActorB sends "Goodbye!" → ActorA terminates system
```

### Key Components

| File | Purpose |
|------|---------|
| `MessageA` | Carries a `String` text |
| `MessageB` | Carries an `Integer` number |
| `ActorA` | Initiates conversation, handles both message types |
| `ActorB` | Responds to messages, sends termination signal |
| `Main` | Bootstraps the actor system |

---

## 🚀 How to Run

### Prerequisites
- **Java 8+** (project targets Java 1.8)
- One of: Maven, Gradle, or SBT

### Option 1: Maven (Recommended)
```bash
# Build
mvn compile

# Run
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Option 2: Gradle
```bash
# Build
./gradlew build

# Run
./gradlew run
```

### Option 3: SBT
```bash
# Build
./sbt compile

# Run
./sbt run
```

### Option 4: From IDE (IntelliJ IDEA)
1. Open project as **Maven/Gradle/SBT** project
2. Right-click `Main.java` → **Run 'Main.main()'**

---

## 🔧 Build Configurations

All three build files define the same dependencies:
- **Akka Typed 2.6.5** (Scala 2.13)
- **Logback Classic 1.2.3** for logging
- **JUnit 4.12** + **Akka TestKit** for testing

---

## 📝 Logging

Configured via `logback.xml`:
- Async console appender
- Pattern: `[timestamp] [level] [logger] [thread] [akkaSource] - message`
- Root level: `INFO`

---

## 🧪 Testing

Run tests with any build tool:
```bash
mvn test          # Maven
./gradlew test    # Gradle
./sbt test        # SBT
```

---

## ⚠️ Notes for Beginners

1. **Classic vs Typed API**: This example uses the **classic (untyped) Actor API** (`AbstractActor`), not the newer Typed API. The build includes `akka-actor-typed` but the code uses `akka.actor.AbstractActor`.

2. **ActorSystem Creation**: Uses `ActorSystem.create()` (deprecated in newer versions). Modern code would use `ActorSystem.create(Behavior, "name")`.

3. **Termination**: Press **ENTER twice** in console to gracefully shutdown.

4. **Akka Version**: 2.6.5 is older (current is 2.8+). Consider upgrading for new projects.

---

## 📚 Learning Resources

- [Akka Typed Documentation](https://doc.akka.io/docs/akka/2.6/typed/index.html)
- [Akka Classic Actors](https://doc.akka.io/docs/akka/2.6/actors.html)
- [Actor Model Concepts](https://doc.akka.io/docs/akka/2.6/typed/guide/actors.html)