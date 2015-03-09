/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.navigation;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import org.eclipse.che.ide.navigation.NavigateToFilePresenter;
import org.eclipse.che.ide.navigation.NavigateToFileView;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.websocket.MessageBus;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Ignore;
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

    public static final String PROJECT_NAME      = "test";
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
    private DialogFactory           dialogFactory;
    @Mock
    private DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    @Mock
    private NotificationManager     notificationManager;

    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(project);

        presenter = new NavigateToFilePresenter(view, appContext, eventBus, messageBus, anyString(), dtoUnmarshallerFactory, dialogFactory,
                                                localizationConstant);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).showDialog();
        verify(view).clearInput();
    }

    @Ignore
    @Test
    public void testOnFileSelected() throws Exception {
        String displayName = FILE_IN_ROOT_NAME + " (" + PROJECT_NAME + ")";
        when(view.getItemPath()).thenReturn(displayName);

        presenter.showDialog();
        presenter.onFileSelected();

        verify(view).close();
        verify(view).getItemPath();
        verify(eventBus).fireEvent((FileEvent)anyObject());
    }
}
