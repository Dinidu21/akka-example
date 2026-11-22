package com.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

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

    private void onMessageB(MessageB msg) {
        System.out.println("Actor B received Message B: " + msg.number + " from " + getSender());
        int additional = msg.number;
        for (int i = 0; i < 10; i++) {
            additional += i;
        }
        System.out.println("Actor B additional sum: " + additional);
        getSender().tell(new MessageB(additional), getSelf());
    }
}