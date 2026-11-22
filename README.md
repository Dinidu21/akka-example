# Akka Example — Scenario, Build & Run Notes

## Scenario: What happens when the code runs

This example demonstrates the Akka actor model using the **classic (untyped) actor API**. Two actors, `ActorA` and `ActorB`, exchange immutable messages asynchronously in a ping-pong style conversation that eventually shuts down the actor system.

### Message flow (step by step)

```
┌─────────┐                                          ┌─────────┐
│   Main  │                                          │ ActorA  │
└────┬────┘                                          └────┬────┘
     │                                                    │
     │ 1. ActorSystem.create()                            │
     │ 2. actorOf(ActorA) ───────────────────────────────►│
     │                                                    │
     │ 3. tell(MessageA("Starting"), self) ─────────────►│
     │                                                    │◄────┐
     │                                                    │     │ 4. creates ActorB
     │                                                    │     │ 5. tell(MessageA("Hello!"), self)
     │                                                    │─────┼────────────────►│
     │ ActorB                                             │     │                 ActorB
     │                                                    │     │                 │
     │                                                    │     │◄────────────────│
     │                                                    │     │ 6. tell(MessageB(42), self)
     │                                                    │◄────┼─────────────────┤
     │ 7. tell(MessageB(999), self) ─────────────────────►│     │                 │
     │                                                    │     │◄────────────────│
     │                                                    │     │ 8. tell(MessageA("Goodbye!"), self)
     │                                                    │◄────┼─────────────────┤
     │ 9. ActorA sees "Goodbye!" → system.terminate()     │     │                 │
     │                                                    │     │
     │ 10. System.in.read() returns                       │     │
     │ 11. system.terminate()                             │     │
     │ 12. print "Terminated."                            │     │
     └────────────────────────────────────────────────────┴─────┘
```

### Detailed walkthrough

| Step | Actor | Action | Explanation |
|------|-------|--------|-------------|
| 1 | Main | `ActorSystem.create()` | Starts the actor system — the container that hosts all actors, threads, and dispatchers. |
| 2 | Main | `system.actorOf(Props.create(ActorA.class))` | Creates `ActorA` under the `/user` guardian and gets back an `ActorRef` handle (`actorARef`). |
| 3 | Main | `actorARef.tell(new MessageA("Starting"), actorARef)` | Sends the first message asynchronously. `tell` is fire-and-forget; the sender is `actorARef` itself. |
| 4 | ActorA | Receives `MessageA("Starting")` | Logs receipt. Since text is not `"Goodbye!"`, it creates `ActorB` dynamically via `system.actorOf(...)`. |
| 5 | ActorA | `actorBRef.tell(new MessageA("Hello!"), getSelf())` | Sends `MessageA("Hello!")` to `ActorB`. `ActorA` identifies itself as the sender so `ActorB` knows where to reply. |
| 6 | ActorB | Receives `MessageA("Hello!")` | Logs receipt, then replies to the sender (`ActorA`) with `MessageB(42)`. |
| 7 | ActorA | Receives `MessageB(42)` | Logs receipt, then replies to the sender (`ActorB`) with `MessageB(999)`. |
| 8 | ActorB | Receives `MessageB(999)` | Since `number == 999`, replies to the sender (`ActorA`) with `MessageA("Goodbye!")` — the shutdown signal. |
| 9 | ActorA | Receives `MessageA("Goodbye!")` | Calls `getContext().getSystem().terminate()` to shut down the whole actor system. |
| 10 | Main | `System.in.read()` returns | The main thread was blocked waiting for ENTER; once it returns, the `finally` block runs. |
| 11 | Main | `system.terminate()` | Gracefully shuts down the actor system and releases resources. |
| 12 | Main | `System.out.println("Terminated.")` | Prints the final message. Exit code `0`. |

### Concurrent work loops

Each message handler also runs a local loop printing 10 lines (`Actor A doing work 0..9`, `Actor A doing more work 0..9`, etc.) to simulate CPU-bound processing. Because actors process one message at a time per actor but run concurrently across actors, these work lines interleave in the console output.

### Key Akka concepts illustrated

- **ActorSystem**: heavyweight container; entry point for creating actors.
- **ActorRef**: opaque, location-transparent handle to an actor. You never call methods on the actor object directly; you only `tell` messages to its `ActorRef`.
- **Props**: immutable configuration describing how to create an actor instance (class + constructor).
- **tell(message, sender)**: asynchronous, non-blocking message send ("fire and forget").
- **getSender()**: the `ActorRef` of whoever sent the message currently being processed — used for replies.
- **getSelf()**: this actor's own `ActorRef` — used when the actor wants to identify itself as the sender.
- **getContext()**: access to the actor's context (system, parent, children, self).
- **receiveBuilder()**: builds a `Receive` partial function matching incoming message types to handler methods.
- **Immutable messages**: `MessageA` (String `text`) and `MessageB` (Integer `number`) are plain immutable POJOs — shared safely between actors without locks.

## Build & run

No `mvn` or `gradle` on `PATH`, so compile and run directly with `javac`/`java` using the dependencies cached in the local Maven repository:

```
C:\Users\User\.m2\repository
```

### Compile

```powershell
javac -encoding UTF-8 -d target/classes `
  -cp "C:\Users\User\.m2\repository\com\typesafe\akka\akka-actor-typed_2.13\2.6.5\akka-actor-typed_2.13-2.6.5.jar;
       C:\Users\User\.m2\repository\com\typesafe\akka\akka-actor_2.13\2.6.5\akka-actor_2.13-2.6.5.jar;
       C:\Users\User\.m2\repository\com\typesafe\config\1.4.0\config-1.4.0.jar;
       C:\Users\User\.m2\repository\org\scala-lang\scala-library\2.13.1\scala-library-2.13.1.jar" `
  src\main\java\com\example\*.java
```

### Run

The program waits for ENTER input (`System.in.read()`), so pipe an empty line to it:

```powershell
echo "" | java `
  -cp "target/classes;
       C:\Users\User\.m2\repository\com\typesafe\akka\akka-actor-typed_2.13\2.6.5\akka-actor-typed_2.13-2.6.5.jar;
       C:\Users\User\.m2\repository\com\typesafe\akka\akka-actor_2.13\2.6.5\akka-actor_2.13-2.6.5.jar;
       C:\Users\User\.m2\repository\com\typesafe\config\1.4.0\config-1.4.0.jar;
       C:\Users\User\.m2\repository\org\scala-lang\scala-library\2.13.1\scala-library-2.13.1.jar;
       C:\Users\User\.m2\repository\org\slf4j\slf4j-api\1.7.30\slf4j-api-1.7.30.jar" `
  com.example.Main
```

### Expected output (representative)

```
Press ENTER twice to end program.
Actor A received Message A : Starting from Actor[akka://default/user/$a#...]
Actor A doing work 0
Actor A doing work 1
...
Actor A doing work 9
Actor B received Message A : Hello! from Actor[akka://default/user/$a#...]
Actor B doing work 0
...
Actor B doing work 9
Actor A received Message B : 42 from Actor[akka://default/user/$b#...]
Actor A doing more work 0
...
Actor A doing more work 9
Actor B received Message B : 999 from Actor[akka://default/user/$a#...]
Actor B doing more work 0
...
Actor B doing more work 9
Actor A received Message A : Goodbye! from Actor[akka://default/user/$b#...]
Actor A doing work 0
...
Actor A doing work 9
Terminated.
```

Exit code: `0`.

> The exact interleaving of "doing work" lines varies between runs because the two actors process messages concurrently on different dispatcher threads.

## Notes

- `SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"` — harmless; Akka defaults to a NOP logger unless a binding such as `logback-classic` is on the classpath.
- `sun.misc.Unsafe::objectFieldOffset` warnings — harmless on Java 25; Akka 2.6.5 uses `sun.misc.Unsafe` internally.
- `slf4j-api-1.7.30.jar` is required on the runtime classpath; without it `ActorSystem.create()` throws `NoClassDefFoundError: org/slf4j/LoggerFactory`.

## Source files

```
src/main/java/com/example/
  Main.java        # entry point; creates ActorSystem, creates ActorA, sends "Starting", terminates system
  ActorA.java      # handles MessageA (creates ActorB, sends "Hello!", shutdown on "Goodbye!") and MessageB (replies 999)
  ActorB.java      # handles MessageA (replies 42) and MessageB (replies "Goodbye!" on 999, else 999)
  MessageA.java    # immutable message with String text
  MessageB.java    # immutable message with Integer number
target/classes/    # compiled output
pom.xml / build.gradle / build.sbt  # build descriptors (Maven/Gradle/sbt)
```
