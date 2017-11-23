package com.github.nandujawale.akkajava;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.nandujawale.akkajava.Messages.LogAnalysisResult;
import com.github.nandujawale.akkajava.Messages.LogFile;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class Main {

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("HAProxyLogAnalysis");

        ActorRef analyzer = system.actorOf(Props.create(HAProxyLogAnalyzer.class), "HAProxyLogAnalyzer");

        LogFile message = new LogFile("D:\\tmp\\haproxy.log");

        Timeout timeout = new Timeout(10, TimeUnit.SECONDS);
        // long time = -System.currentTimeMillis();
        Future<Object> future = Patterns.ask(analyzer, message, timeout);
        LogAnalysisResult result = (LogAnalysisResult)Await.result(future, timeout.duration());
        // time += System.currentTimeMillis();
        printResult(result);
        // System.out.println("\nTook " + time + " millisecs");

        Await.result(system.terminate(), Duration.create(5, "seconds"));
    }

    private static void printResult(LogAnalysisResult result) {
        Map<String, Long> data = result.getData();
        System.out.println("Result:");
        data.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
