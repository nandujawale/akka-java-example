package com.github.nandujawale.akkajava;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.github.nandujawale.akkajava.Messages.LogAnalysisResult;
import com.github.nandujawale.akkajava.Messages.LogFile;
import com.github.nandujawale.akkajava.Messages.LogMessage;
import com.github.nandujawale.akkajava.Messages.LogMessageResult;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class HAProxyLogAnalyzer extends AbstractActor {

    private final Map<String, Long> analysisResult = new HashMap<>();

    private long totalLines;
    private long processedLines;

    private ActorRef requestor = null;

    private final SupervisorStrategy STRATEGY = new OneForOneStrategy(2,
        Duration.create(1, TimeUnit.MINUTES),
        false,
        DeciderBuilder
            .match(IllegalArgumentException.class, e -> {
                processedLines++;
                System.out.println("Stopping actor for " + e);
                return SupervisorStrategy.stop();
            })
            .matchAny(o -> {
                System.out.println("Cannot handle " + o + ", escalating...");
                return SupervisorStrategy.escalate();
            })
            .build());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(LogFile.class, this::onLogFile)
            .match(LogMessageResult.class, this::onLogMessageResult)
            .build();
    }

    private void onLogFile(LogFile message) throws IOException {
        String fileName = message.getFileName();
        List<String> lines = FileUtils.readLines(new File(fileName), StandardCharsets.UTF_8);
        totalLines = lines.size();
        System.out.println("Total " + totalLines + " lines to analyze...");

        requestor = sender();

        int count = 0;
        for (String line : lines) {
            count++;
            ActorRef processor = context().actorOf(Props.create(LogMessageAnalyzer.class), "LogMessageAnalyzer_" + count);
            processor.tell(new LogMessage(line), self());
        }
    }

    private void onLogMessageResult(LogMessageResult message) {
        String ip = message.getIpAddress();
        Long count = analysisResult.getOrDefault(ip, 0L);
        count++;
        analysisResult.put(ip, count);

        processedLines++;
        if (totalLines == processedLines) {
            requestor.tell(new LogAnalysisResult(analysisResult), self());
        }
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }
}
