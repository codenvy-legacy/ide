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
package com.codenvy.ide.navigation;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link NavigateToFilePresenter}.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigateToFileTest {

    public static final String PROJECT_PATH      = "/test";
    public static final String PROJECT_NAME      = "test";
    public static final String FOLDER_NAME       = "folder";
    public static final String FILE_IN_ROOT_NAME = "pom.xml";

    @Mock
    private NavigateToFileView      view;
    @Mock
    private AppContext              appContext;
    @Mock
    private CurrentProject          project;
    @Mock
    private EventBus                eventBus;
    private NavigateToFilePresenter presenter;
    @Mock
    private MessageBus              messageBus;
    @Mock
    private ProjectServiceClient    projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    @Mock
    private NotificationManager     notificationManager;

    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(project);

        presenter = new NavigateToFilePresenter(view, appContext, eventBus, messageBus, anyString(), dtoUnmarshallerFactory);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).showDialog();
        verify(view).clearInput();
    }

    @Test
    public void testOnFileSelected() throws Exception {
        String displayName = FILE_IN_ROOT_NAME + " (" + PROJECT_NAME + ")";
        when(view.getItemPath()).thenReturn(displayName);

        presenter.showDialog();
        presenter.onFileSelected();

        verify(view).close();
        verify(view).getItemPath();
//        verify(eventBus).fireEvent((FileEvent)anyObject());
    }
}
