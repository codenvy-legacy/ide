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
package com.codenvy.ide.projectimport.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectProblem;
import com.codenvy.api.vfs.gwt.client.VfsServiceClient;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projectimport.wizard.ImportProjectNotificationSubscriber;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ImportWizard}.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportWizardTest {
    private static final String PROJECT_NAME = "project1";

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Item>>              callbackCaptorForItem;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<ProjectDescriptor>> callbackCaptorForProject;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Void>>              callbackCaptorForVoid;

    @Mock
    private ProjectServiceClient                projectServiceClient;
    @Mock
    private VfsServiceClient                    vfsServiceClient;
    @Mock
    private DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    @Mock
    private DtoFactory                          dtoFactory;
    @Mock
    private EventBus                            eventBus;
    @Mock
    private CoreLocalizationConstant            localizationConstant;
    @Mock
    private ImportProjectNotificationSubscriber importProjectNotificationSubscriber;

    @Mock
    private ImportProject           importProject;
    @Mock
    private NewProject              newProject;
    @Mock
    private Wizard.CompleteCallback completeCallback;

    @InjectMocks
    private ImportWizard wizard;

    @Before
    public void setUp() {
        when(newProject.getName()).thenReturn(PROJECT_NAME);
        when(importProject.getProject()).thenReturn(newProject);
    }

    @Test
    public void shouldInvokeCallbackWhenFolderAlreadyExists() throws Exception {
        wizard.complete(completeCallback);

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());

        AsyncRequestCallback<Item> callback = callbackCaptorForItem.getValue();
        Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
        onSuccess.invoke(callback, mock(Item.class));

        verify(completeCallback).onFailure(any(Throwable.class));
    }

    @Test
    public void shouldImportAndOpenProject() throws Exception {
        wizard.complete(completeCallback);

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());

        AsyncRequestCallback<Item> itemCallback = callbackCaptorForItem.getValue();
        Method onFailure = GwtReflectionUtils.getMethod(itemCallback.getClass(), "onFailure");
        onFailure.invoke(itemCallback, mock(Throwable.class));

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(false), eq(importProject), callbackCaptorForProject.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptorForProject.getValue();
        Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
        onSuccess.invoke(callback, mock(ProjectDescriptor.class));

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldImportAndOpenProjectForConfiguring() throws Exception {
        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        List<ProjectProblem> problems = mock(List.class);
        when(problems.isEmpty()).thenReturn(false);
        when(projectDescriptor.getProblems()).thenReturn(problems);

        wizard.complete(completeCallback);

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());

        AsyncRequestCallback<Item> itemCallback = callbackCaptorForItem.getValue();
        Method onFailure = GwtReflectionUtils.getMethod(itemCallback.getClass(), "onFailure");
        onFailure.invoke(itemCallback, mock(Throwable.class));

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(false), eq(importProject), callbackCaptorForProject.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptorForProject.getValue();
        Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
        onSuccess.invoke(callback, projectDescriptor);

        verify(eventBus, times(2)).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }
}
