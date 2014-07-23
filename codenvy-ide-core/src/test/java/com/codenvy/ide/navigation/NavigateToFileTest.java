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
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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

    public static final String PROJECT_PATH          = "/test";
    public static final String PROJECT_NAME          = "test";
    public static final String FOLDER_NAME           = "folder";
    public static final String FILE_1_IN_FOLDER_NAME = "file1.txt";
    public static final String FILE_2_IN_FOLDER_NAME = "file2.html";
    public static final String FILE_IN_ROOT_NAME     = "pom.xml";

    @Mock
    private NavigateToFileView      view;
    @Mock
    private ResourceProvider        resourceProvider;
    @Mock
    private Project                 project;
    @Mock
    private EventBus
            eventBus;
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
        Array<Resource> children = Collections.createArray();

        Folder folder = Mockito.mock(Folder.class);
        Mockito.when(folder.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME);

        File fileInFolder1 = Mockito.mock(File.class);
        Mockito.when(fileInFolder1.getName()).thenReturn(FILE_1_IN_FOLDER_NAME);
        Mockito.when(fileInFolder1.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME + "/" + FILE_1_IN_FOLDER_NAME);

        File fileInFolder2 = Mockito.mock(File.class);
        Mockito.when(fileInFolder2.getName()).thenReturn(FILE_2_IN_FOLDER_NAME);
        Mockito.when(fileInFolder2.getPath()).thenReturn(PROJECT_PATH + "/" + FOLDER_NAME + "/" + FILE_2_IN_FOLDER_NAME);

        File fileInRoot = Mockito.mock(File.class);
        Mockito.when(fileInRoot.getName()).thenReturn(FILE_IN_ROOT_NAME);
        Mockito.when(fileInRoot.getPath()).thenReturn(PROJECT_PATH + "/" + FILE_IN_ROOT_NAME);

        Folder parentFolder = mock(Folder.class);

        when(project.getChildren()).thenReturn(children);
        when(project.getParent()).thenReturn(parentFolder);
        when(resourceProvider.getActiveProject()).thenReturn(project);
        when(parentFolder.getName()).thenReturn("");

        presenter = new NavigateToFilePresenter(view, resourceProvider, eventBus, messageBus, anyString(), dtoUnmarshallerFactory,
                                                notificationManager, localizationConstant);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).showDialog();
        verify(view).clearInput();
        verify(view).focusInput();
    }

    @Test
    public void testOnFileSelected() throws Exception {
        String displayName = FILE_IN_ROOT_NAME + " (" + PROJECT_NAME + ")";
        when(view.getItemPath()).thenReturn(displayName);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mock(File.class));
                return callback;
            }
        }).when(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());

        presenter.showDialog();
        presenter.onFileSelected();

        verify(view).close();
        verify(view).getItemPath();
        verify(eventBus).fireEvent((FileEvent)anyObject());
    }
}
