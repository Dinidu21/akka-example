package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * ActorA performs the first half of the prime search (2 to 50).
 *
 * Concept: An actor processes one message at a time from its mailbox. It can
 * create child actors and send messages to them by their ActorRef.
 *
 * Message handling:
 *   - "start": finds and prints primes between 2 and 50, creates ActorB, and
 *     sends ActorB a "continue" message.
 *   - "Goodbye!": announces the task is completed and terminates the system.
 */
public class ActorA extends AbstractActor {

    private final ActorSystem system;

    public ActorA(ActorSystem system) {
        this.system = system;
    }

    public static Props props(ActorSystem system) {
        return Props.create(ActorA.class, () -> new ActorA(system));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::onString)
                .build();
    }

    private void onString(String msg) {
        if (msg.equals("start")) {
            // Find and print prime numbers between 2 and 50.
            System.out.println("Actor A: Prime numbers between 2 and 50:");
            for (int n = 2; n <= 50; n++) {
                if (isPrime(n)) {
                    System.out.println(n);
                }
            }
            System.out.println("Actor A: done with 2-50.");

            // Create ActorB and tell it to "continue".
            ActorRef actorBRef = system.actorOf(Props.create(ActorB.class), "actorB");
            actorBRef.tell("continue", getSelf());
        } else if (msg.equals("Goodbye!")) {
            // Task completed: announce and turn off the system.
            System.out.println("Actor A: Task completed! Prime search from 2 to 100 is finished.");
            system.terminate();
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}