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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.run.RunController;
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
    protected RunController              runController;
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
    protected AppContext                 appContext;
    @Mock
    protected RunnerLocalizationConstant constant;
    @Mock
    protected CurrentProject             activeProject;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(activeProject);
    }
}
