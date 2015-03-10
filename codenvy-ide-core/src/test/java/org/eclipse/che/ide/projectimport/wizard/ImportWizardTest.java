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
package org.eclipse.che.ide.projectimport.wizard;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportResponse;
import org.eclipse.che.api.project.shared.dto.NewProject;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectProblem;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClient;
import org.eclipse.che.api.vfs.shared.dto.Item;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.test.GwtReflectionUtils;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private ArgumentCaptor<AsyncRequestCallback<Item>>           callbackCaptorForItem;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<ImportResponse>> callbackCaptorForProject;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Void>>           callbackCaptorForVoid;

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
        GwtReflectionUtils.callOnSuccess(callback, mock(Item.class));

        verify(completeCallback).onFailure(any(Throwable.class));
    }

    @Test
    public void shouldImportAndOpenProject() throws Exception {
        wizard.complete(completeCallback);

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());

        AsyncRequestCallback<Item> itemCallback = callbackCaptorForItem.getValue();
        GwtReflectionUtils.callOnFailure(itemCallback, mock(Throwable.class));

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(false), eq(importProject), callbackCaptorForProject.capture());

        ImportResponse importResponse = mock(ImportResponse.class);
        when(importResponse.getProjectDescriptor()).thenReturn(mock(ProjectDescriptor.class));
        AsyncRequestCallback<ImportResponse> callback = callbackCaptorForProject.getValue();
        GwtReflectionUtils.callOnSuccess(callback, importResponse);

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    //  @Test
    public void shouldImportAndOpenProjectForConfiguring() throws Exception {
        ImportResponse importResponse = mock(ImportResponse.class);
        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        List<ProjectProblem> problems = mock(List.class);
        when(problems.isEmpty()).thenReturn(false);
        when(importResponse.getProjectDescriptor()).thenReturn(projectDescriptor);
        when(projectDescriptor.getProblems()).thenReturn(problems);

        wizard.complete(completeCallback);

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());

        AsyncRequestCallback<Item> itemCallback = callbackCaptorForItem.getValue();
        GwtReflectionUtils.callOnFailure(itemCallback, mock(Throwable.class));

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(false), eq(importProject), callbackCaptorForProject.capture());

        AsyncRequestCallback<ImportResponse> callback = callbackCaptorForProject.getValue();
        GwtReflectionUtils.callOnSuccess(callback, importResponse);

        verify(eventBus, times(2)).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }
}
