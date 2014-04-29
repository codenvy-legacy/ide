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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * Base test for runner extension.
 *
 * @author Artem Zatsarynnyy
 */
@GwtModule("com.codenvy.ide.extension.runner.Runner")
public abstract class BaseTest extends GwtTestWithMockito {
    @Mock
    protected RunnerController           runnerController;
    @Mock
    protected RunnerConsolePresenter     runnerConsolePresenter;
    @Mock
    protected RunnerServiceClient        service;
    @Mock
    protected MessageBus                 messageBus;
    @Mock
    protected NotificationManager        notificationManager;
    @Mock
    protected DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    @Mock
    protected ResourceProvider           resourceProvider;
    @Mock
    protected RunnerLocalizationConstant constant;
    @Mock
    protected Project                    activeProject;

    @Before
    public void setUp() {
        when(resourceProvider.getActiveProject()).thenReturn(activeProject);
    }
}
