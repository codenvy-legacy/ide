/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;

/**
 * This class listens for log messages from the server
 * and process it. Logic of this class is slightly complicated
 * since we can't guaranty correct order of messages and
 * delivery it from the server over WebSocket connection.
 * So messages may be received in shuffled order and some
 * messages may be never received.
 *
 * @author Artem Zatsarynnyy
 */
class LogMessagesHandler extends SubscriptionHandler<LogMessage> {
    private final ApplicationProcessDescriptor applicationProcessDescriptor;
    private final RunnerConsolePresenter       console;
    private final MessageBus                   messageBus;
    private       int                          lastPrintedMessageNum;
    private IntegerMap<LogMessage> postponedMessages = Collections.createIntegerMap();
    private Timer flushTimer;

    LogMessagesHandler(ApplicationProcessDescriptor applicationProcessDescriptor, RunnerConsolePresenter console, MessageBus messageBus) {
        super(new LogMessageUnmarshaller());
        this.applicationProcessDescriptor = applicationProcessDescriptor;
        this.console = console;
        this.messageBus = messageBus;
        flushTimer = new Timer() {
            @Override
            public void run() {
                printAllPostponedMessages();
            }
        };
    }

    @Override
    protected void onMessageReceived(LogMessage result) {
        if (result.num == lastPrintedMessageNum + 1) {
            flushTimer.cancel();
            printLine(result);
        } else if (result.num > lastPrintedMessageNum) {
            postponedMessages.put(result.num, result);
        }

        printNextPostponedMessages();
        flushTimer.schedule(5000);
    }

    /** Print next postponed messages with contiguous line numbers. */
    private void printNextPostponedMessages() {
        LogMessage nextLogMessage = postponedMessages.get(lastPrintedMessageNum + 1);
        while (nextLogMessage != null) {
            printLine(nextLogMessage);

            postponedMessages.erase(nextLogMessage.num);
            nextLogMessage = postponedMessages.get(nextLogMessage.num + 1);
        }
    }

    /** Print all postponed messages in correct order. */
    private void printAllPostponedMessages() {
        for (int i = lastPrintedMessageNum + 1; !postponedMessages.isEmpty(); i++) {
            LogMessage nextLogMessage = postponedMessages.get(i);
            if (nextLogMessage != null) {
                printLine(nextLogMessage);
                postponedMessages.erase(i);
            }
        }
    }

    private void printLine(LogMessage logMessage) {
        console.print(logMessage.text);
        lastPrintedMessageNum = logMessage.num;
    }

    @Override
    protected void onErrorReceived(Throwable throwable) {
        try {
            messageBus.unsubscribe(RunnerController.RUNNER_OUTPUT_CHANNEL + applicationProcessDescriptor.getProcessId(), this);
            Log.error(LogMessagesHandler.class, throwable);
        } catch (WebSocketException e) {
            Log.error(LogMessagesHandler.class, e);
        }
    }
}

class LogMessageUnmarshaller implements Unmarshallable<LogMessage> {
    LogMessage logMessage;

    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
        if (jsonObject != null && jsonObject.containsKey("line")) {
            final int lineNumber = (int)jsonObject.get("num").isNumber().doubleValue();
            final String text = jsonObject.get("line").isString().stringValue();
            logMessage = new LogMessage(lineNumber, text);
        }
    }

    @Override
    public LogMessage getPayload() {
        return logMessage;
    }
}

class LogMessage {
    int    num;
    String text;

    LogMessage(int lineNumber, String text) {
        this.num = lineNumber;
        this.text = text;
    }
}
