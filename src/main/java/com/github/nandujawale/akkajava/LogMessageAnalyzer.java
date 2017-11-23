package com.github.nandujawale.akkajava;

import com.github.nandujawale.akkajava.Messages.LogMessage;
import com.github.nandujawale.akkajava.Messages.LogMessageResult;

import akka.actor.AbstractActor;

public class LogMessageAnalyzer extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(LogMessage.class, this::onLogMessage)
            .build();
    }

    private void onLogMessage(LogMessage message) {
        String data = message.getData();
        String ip = data.split(" ")[3].split(":")[0];
        sender().tell(new LogMessageResult(ip), self());
    }
}
