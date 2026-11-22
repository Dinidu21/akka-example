package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("akka-example");
        ActorRef actorARef = system.actorOf(Props.create(ActorA.class), "actorA");
        actorARef.tell(new MessageA("Starting"), ActorRef.noSender());

        try {
            Await.result(system.whenTerminated(), Duration.create(60, TimeUnit.SECONDS));
            System.out.println("Actor system terminated.");
        } catch (Exception e) {
            System.err.println("Error waiting for termination: " + e.getMessage());
        }
    }

}