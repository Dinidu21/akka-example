package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * ActorA performs the first half of the distributed work.
 *
 * Concept: An actor is an object that encapsulates state and behavior, and
 * processes one message at a time from its mailbox. It replies by sending
 * messages back to the sender (getSender()).
 *
 * MessageA handler ("Starting" / "Goodbye!"):
 *   - On "Starting": sums all "i" values (0..9), prints the sum, creates ActorB,
 *     and sends ActorB a MessageB carrying that sum.
 *   - On "Goodbye!": terminates the actor system.
 *
 * MessageB handler (reply from ActorB):
 *   - Prints the received result, then sends a MessageA("Goodbye!") to itself
 *     and terminates the system.
 */
public class ActorA extends AbstractActor {

    public static Props props() {
        return Props.create(ActorA.class, ActorA::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageA.class, this::onMessageA)
                .match(MessageB.class, this::onMessageB)
                .build();
    }

    /**
     * Handles incoming MessageA.
     */
    private void onMessageA(MessageA msg) {
        System.out.println("Actor A received Message A: " + msg.text + " from " + getSender());

        if (msg.text.equalsIgnoreCase("Goodbye!")) {
            // h. ActorA tells the Akka actor system to terminate.
            System.out.println("Actor A received Goodbye! Terminating system.");
            getContext().getSystem().terminate();
        } else {
            // Requirement 1: ActorA does the work (sum all "i" values) before responding.
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }
            System.out.println("Actor A work sum: " + sum);

            // d. ActorA creates ActorB and sends it a MessageB of the sum (result) value.
            ActorRef actorBRef = getContext().getSystem().actorOf(Props.create(ActorB.class), "actorB");
            actorBRef.tell(new MessageB(sum), getSelf());
        }
    }

    /**
     * Handles incoming MessageB (the result coming back from ActorB).
     */
    private void onMessageB(MessageB msg) {
        System.out.println("Actor A received Message B: " + msg.number + " from " + getSender());

        // h. ActorA sends a MessageA of "Goodbye!" to the system and terminates it.
        getSelf().tell(new MessageA("Goodbye!"), getSelf());
        getContext().getSystem().terminate();
    }
}