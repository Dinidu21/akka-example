package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * ActorB performs the second half of the distributed work.
 *
 * Concept: Actors are location-transparent and asynchronous. ActorB receives a
 * message from ActorA, does its own work, and sends a result back to the sender.
 *
 * MessageA handler: acknowledges receipt (not used in the main flow).
 *
 * MessageB handler:
 *   - Prints the sum received from ActorA.
 *   - Requirement 1: Adds its own "i" values to the received sum.
 *   - g. Replies with a MessageB containing the resulting sum to the sender (ActorA).
 */
public class ActorB extends AbstractActor {

    public static Props props() {
        return Props.create(ActorB.class, ActorB::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageA.class, this::onMessageA)
                .match(MessageB.class, this::onMessageB)
                .build();
    }

    private void onMessageA(MessageA msg) {
        System.out.println("Actor B received Message A: " + msg.text + " from " + getSender());
    }

    /**
     * Handles incoming MessageB from ActorA.
     */
    private void onMessageB(MessageB msg) {
        // e. ActorB receives the MessageB from ActorA (prints the received message).
        System.out.println("Actor B received Message B: " + msg.number + " from " + getSender());

        // f. ActorB starts adding his "i" values to the sum value received from ActorA.
        int additional = msg.number;
        for (int i = 0; i < 10; i++) {
            additional += i;
        }
        System.out.println("Actor B additional sum: " + additional);

        // g. ActorB replies with a MessageB with the resulting sum.
        getSender().tell(new MessageB(additional), getSelf());
    }
}