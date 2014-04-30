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
import com.codenvy.ide.extension.runner.client.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link LogMessagesHandler} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class LogMessagesHandlerTest extends BaseTest {
    private LogMessagesHandler logMessagesHandler;
    private List<LogMessage> orderedMessages = new ArrayList<>(100);

    @Before
    public void setUp() {
        super.setUp();
        logMessagesHandler = new LogMessagesHandler(mock(ApplicationProcessDescriptor.class), runnerConsolePresenter, messageBus);

        orderedMessages.clear();
        for (int i = 0; i < 100; i++) {
            orderedMessages.add(new LogMessage(i + 1, "message#" + i));
        }
    }

    @Test
    public void shouldPrintMessagesInCorrectOrder() throws Exception {
        List<LogMessage> shuffledMessages = new ArrayList<>(orderedMessages);
        Collections.shuffle(shuffledMessages);

        for (LogMessage logMessage : shuffledMessages) {
            logMessagesHandler.onMessageReceived(logMessage);
        }

        InOrder inOrder = inOrder(runnerConsolePresenter);
        for (LogMessage logMessage : orderedMessages) {
            inOrder.verify(runnerConsolePresenter, times(1)).print(logMessage.text);
        }
    }

    @Test
    public void shouldSkipLostMessages() throws Exception {
        List<LogMessage> listWithMissedMessages = new ArrayList<>(orderedMessages);
        listWithMissedMessages.remove(75);
        listWithMissedMessages.remove(50);
        listWithMissedMessages.remove(25);

        for (LogMessage logMessage : listWithMissedMessages) {
            logMessagesHandler.onMessageReceived(logMessage);
        }

        for (LogMessage logMessage : orderedMessages) {
            final int index = orderedMessages.indexOf(logMessage);
            if (index == 25 || index == 50 || index == 75) {
                verify(runnerConsolePresenter, never()).print(logMessage.text);
            } else {
                verify(runnerConsolePresenter, timeout(5000).times(1)).print(logMessage.text);
            }
        }
    }

}
