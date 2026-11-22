package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

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

    private void onMessageA(MessageA msg) {
        System.out.println("Actor A received Message A: " + msg.text + " from " + getSender());
        if (msg.text.equalsIgnoreCase("Goodbye!")) {
            System.out.println("Actor A received Goodbye! Terminating system.");
            getContext().getSystem().terminate();
        } else {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }
            System.out.println("Actor A work sum: " + sum);

            ActorRef actorBRef = getContext().getSystem().actorOf(Props.create(ActorB.class), "actorB");
            actorBRef.tell(new MessageB(sum), getSelf());
        }
    }

    private void onMessageB(MessageB msg) {
        System.out.println("Actor A received Message B: " + msg.number + " from " + getSender());
        getSelf().tell(new MessageA("Goodbye!"), getSelf());
        getContext().getSystem().terminate();
    }
}