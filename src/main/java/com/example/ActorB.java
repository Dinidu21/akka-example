package com.example;

import akka.actor.AbstractActor;
import akka.actor.Props;

/**
 * ActorB performs the second half of the prime search (51 to 100).
 *
 * Concept: Actors are asynchronous and location-transparent. ActorB receives a
 * message from ActorA, does its own work, and sends a result back to the sender.
 *
 * Message handling:
 *   - "continue": finds and prints primes between 51 and 100, then sends a
 *     "Goodbye!" message back to the sender (ActorA).
 */
public class ActorB extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::onString)
                .build();
    }

    private void onString(String msg) {
        if (msg.equals("continue")) {
            // Find and print prime numbers between 51 and 100.
            System.out.println("Actor B: Prime numbers between 51 and 100:");
            for (int n = 51; n <= 100; n++) {
                if (isPrime(n)) {
                    System.out.println(n);
                }
            }
            System.out.println("Actor B: done with 51-100.");

            // Send "Goodbye!" back to ActorA.
            getSender().tell("Goodbye!", getSelf());
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