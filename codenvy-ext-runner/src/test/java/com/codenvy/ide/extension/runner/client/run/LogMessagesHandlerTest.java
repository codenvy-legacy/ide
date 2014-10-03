/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.websocket.MessageBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Testing {@link LogMessagesHandler} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@GwtModule("com.codenvy.ide.extension.runner.Runner")
public class LogMessagesHandlerTest extends GwtTestWithMockito {
    @Mock
    protected RunnerConsolePresenter runnerConsolePresenter;
    @Mock
    protected MessageBus             messageBus;

    private LogMessagesHandler logMessagesHandler;
    private List<LogMessage> orderedMessages = new ArrayList<>(100);

    @Before
    public void setUp() {
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
