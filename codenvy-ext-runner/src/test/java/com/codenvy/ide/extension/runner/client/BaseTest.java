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
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Base test for runner extension.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {
    @Mock
    protected RunController              runController;
    @Mock
    protected RunnerServiceClient        service;
    @Mock
    protected NotificationManager        notificationManager;
    @Mock
    protected DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    @Mock
    protected AppContext                 appContext;
    @Mock
    protected RunnerLocalizationConstant constant;
    @Mock
    protected CurrentProject             currentProject;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
    }
}
