package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of the prime-number Akka example.
 *
 * Concept: An ActorSystem is the container that hosts actors. The main program
 * (Main) creates the system, creates ActorA, and kicks off the work by sending
 * a "start" message. Actors then talk to each other by sending messages.
 *
 * Flow:
 *   1. Main creates the ActorSystem and ActorA.
 *   2. Main sends ActorA a "start" message.
 *   3. ActorA finds primes 2-50, then tells ActorB to "continue".
 *   4. ActorB finds primes 51-100, then sends "Goodbye!" back to ActorA.
 *   5. ActorA receives "Goodbye!", announces completion, and shuts down the system.
 */
public class Main {

    public static void main(String[] args) {
        // Create the Akka actor system (the container for all actors).
        ActorSystem system = ActorSystem.create("prime-example");

        // Create ActorA inside the system.
        ActorRef actorARef = system.actorOf(Props.create(ActorA.class, system), "actorA");

        // Send ActorA the "start" message to begin the prime search.
        actorARef.tell("start", ActorRef.noSender());

        // Wait until the actor system terminates (triggered by the "Goodbye!" flow).
        try {
            Await.result(system.whenTerminated(), Duration.create(60, TimeUnit.SECONDS));
            System.out.println("Actor system terminated.");
        } catch (Exception e) {
            System.err.println("Error waiting for termination: " + e.getMessage());
        }
    }
}