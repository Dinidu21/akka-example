package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of the Akka example.
 *
 * Concept: An ActorSystem is the container that hosts actors. Actors communicate
 * by sending messages to each other; they never call each other's methods directly.
 *
 * Flow:
 *   1. Create the ActorSystem.
 *   2. Create ActorA inside that system.
 *   3. Send ActorA a MessageA("Starting") to kick off the work.
 *   4. Wait until the system terminates (triggered by the "Goodbye!" flow in ActorA).
 */
public class Main {

    public static void main(String[] args) {
        // a. Creates the Akka actor system (the container for all actors).
        ActorSystem system = ActorSystem.create("akka-example");

        // b. Create the actor ActorA. actorOf spawns a new actor instance.
        ActorRef actorARef = system.actorOf(Props.create(ActorA.class), "actorA");

        // c. Sends ActorA a MessageA of "Starting". No sender (ActorRef.noSender()).
        actorARef.tell(new MessageA("Starting"), ActorRef.noSender());

        // h (partially). Wait for the actor system to terminate. Termination is
        // triggered by ActorA when it receives the "Goodbye!" message.
        try {
            Await.result(system.whenTerminated(), Duration.create(60, TimeUnit.SECONDS));
            System.out.println("Actor system terminated.");
        } catch (Exception e) {
            System.err.println("Error waiting for termination: " + e.getMessage());
        }
    }

}