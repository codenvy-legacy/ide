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
package com.codenvy.ide.logger;

import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/** @author Anatoliy Bazko */
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsEventLoggerTest {

    @Mock
    private DtoFactory               dtoFactory;
    @Mock
    private UserServiceClient        user;
    @Mock
    private AppContext               appContext;
    @Mock
    private MessageBus               messageBus;
    @Mock
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    @Spy
    @InjectMocks
    private AnalyticsEventLoggerImpl eventLogger;

    @Before
    public void setUp() throws Exception {
        doReturn("workspaceId").when(eventLogger).getWorkspace();
    }

    @Test
    public void shouldLogSimpleAction() {
        doNothing().when(eventLogger).send(anyString(), anyMap());

        ArgumentCaptor<String> eventParam = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> paramsParam = ArgumentCaptor.forClass(Map.class);

        eventLogger.log(new TestedAction(), null, null);

        verify(eventLogger).send(eventParam.capture(), paramsParam.capture());

        String event = eventParam.getValue();
        assertEquals(event, "ide-usage");

        Map<String, String> params = paramsParam.getValue();
        assertEquals(params.size(), 2);
        assertEquals(params.get(AnalyticsEventLoggerImpl.WS_PARAM), "workspaceId");
        assertEquals(params.get(AnalyticsEventLoggerImpl.SOURCE_PARAM), "com.codenvy.ide.logger.AnalyticsEventLoggerTest$TestedAction");
    }

    @Test
    public void shouldLogActionName() {
        doNothing().when(eventLogger).send(anyString(), anyMap());

        ArgumentCaptor<String> eventParam = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> paramsParam = ArgumentCaptor.forClass(Map.class);

        eventLogger.log(new TestedAction(), "IDE: Action");

        verify(eventLogger).send(eventParam.capture(), paramsParam.capture());

        String event = eventParam.getValue();
        assertEquals(event, "ide-usage");

        Map<String, String> params = paramsParam.getValue();
        assertEquals(params.size(), 3);
        assertEquals(params.get(AnalyticsEventLoggerImpl.WS_PARAM), "workspaceId");
        assertEquals(params.get(AnalyticsEventLoggerImpl.SOURCE_PARAM), "com.codenvy.ide.logger.AnalyticsEventLoggerTest$TestedAction");
        assertEquals(params.get(AnalyticsEventLoggerImpl.ACTION_PARAM), "IDE: Action");
    }

    @Test
    public void shouldLogParams() {
        doNothing().when(eventLogger).send(anyString(), anyMap());

        ArgumentCaptor<String> eventParam = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> paramsParam = ArgumentCaptor.forClass(Map.class);

        eventLogger.log(new TestedAction(), "IDE: Action",
                        new HashMap<String, String>() {{
                            put("a", "b");
                        }});

        verify(eventLogger).send(eventParam.capture(), paramsParam.capture());

        String event = eventParam.getValue();
        assertEquals(event, "ide-usage");

        Map<String, String> params = paramsParam.getValue();
        assertEquals(params.size(), 4);
        assertEquals(params.get(AnalyticsEventLoggerImpl.WS_PARAM), "workspaceId");
        assertEquals(params.get(AnalyticsEventLoggerImpl.SOURCE_PARAM), "com.codenvy.ide.logger.AnalyticsEventLoggerTest$TestedAction");
        assertEquals(params.get(AnalyticsEventLoggerImpl.ACTION_PARAM), "IDE: Action");
        assertEquals(params.get("a"), "b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfParamNameExceededLimit() {
        doNothing().when(eventLogger).send(anyString(), anyMap());

        ArgumentCaptor<String> eventParam = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> paramsParam = ArgumentCaptor.forClass(Map.class);

        eventLogger.log(new TestedAction(), "IDE: Action",
                        new HashMap<String, String>() {{
                            put("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "b");
                        }});

        verify(eventLogger).send(eventParam.capture(), paramsParam.capture());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfParamValueExceededLimit() {
        doNothing().when(eventLogger).send(anyString(), anyMap());

        ArgumentCaptor<String> eventParam = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> paramsParam = ArgumentCaptor.forClass(Map.class);

        eventLogger.log(new TestedAction(), "IDE: Action",
                        new HashMap<String, String>() {{
                            put("a",
                                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
                                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
                                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
                                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
                                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                        }});

        verify(eventLogger).send(eventParam.capture(), paramsParam.capture());
    }

    private class TestedAction extends Action {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }
}
