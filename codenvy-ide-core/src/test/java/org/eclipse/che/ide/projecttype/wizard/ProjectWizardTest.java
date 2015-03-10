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
package org.eclipse.che.ide.projecttype.wizard;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportResponse;
import org.eclipse.che.api.project.shared.dto.NewProject;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectUpdate;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;

import org.eclipse.che.ide.projecttype.wizard.ProjectWizard;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.test.GwtReflectionUtils;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.CREATE;
import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.IMPORT;
import static org.eclipse.che.ide.api.project.type.wizard.ProjectWizardMode.UPDATE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ProjectWizard}.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectWizardTest {
    private static final String PROJECT_NAME = "project1";

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<ProjectDescriptor>> callbackCaptor;

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<ImportResponse>> importCallbackCaptor;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Void>>           callbackCaptorForVoid;

    @Mock
    private CoreLocalizationConstant coreLocalizationConstant;
    @Mock
    private ProjectServiceClient     projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    @Mock
    private DtoFactory               dtoFactory;
    @Mock
    private DialogFactory            dialogFactory;
    @Mock
    private EventBus                 eventBus;
    @Mock
    private AppContext               appContext;
    @Mock
    private ImportProject            importProject;
    @Mock
    private NewProject               newProject;
    @Mock
    private Wizard.CompleteCallback  completeCallback;

    private ProjectWizard wizard;

    @Before
    public void setUp() {
        when(newProject.getName()).thenReturn(PROJECT_NAME);
        when(importProject.getProject()).thenReturn(newProject);
    }

    @Test
    public void shouldCreateProject() throws Exception {
        prepareWizard(CREATE);

        wizard.complete(completeCallback);

        verify(projectServiceClient).createProject(eq(PROJECT_NAME), eq(newProject), callbackCaptor.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, mock(ProjectDescriptor.class));

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldInvokeCallbackWhenCreatingFailure() throws Exception {
        prepareWizard(CREATE);
        when(dtoFactory.createDtoFromJson(anyString(), any(Class.class))).thenReturn(mock(ServiceError.class));

        wizard.complete(completeCallback);

        verify(projectServiceClient).createProject(eq(PROJECT_NAME), eq(newProject), callbackCaptor.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));

        verify(completeCallback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldCreateProjectFromTemplate() throws Exception {
        prepareWizard(IMPORT);

        wizard.complete(completeCallback);

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(true), eq(importProject), importCallbackCaptor.capture());

        ImportResponse importResponse = mock(ImportResponse.class);
        when(importResponse.getProjectDescriptor()).thenReturn(mock(ProjectDescriptor.class));
        AsyncRequestCallback<ImportResponse> callback = importCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, importResponse);

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldInvokeCallbackWhenCreatingProjectFromTemplateFailure() throws Exception {
        prepareWizard(IMPORT);
        when(dtoFactory.createDtoFromJson(anyString(), any(Class.class))).thenReturn(mock(ServiceError.class));

        wizard.complete(completeCallback);

        verify(projectServiceClient).importProject(eq(PROJECT_NAME), eq(true), eq(importProject), importCallbackCaptor.capture());

        AsyncRequestCallback<ImportResponse> callback = importCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));

        verify(completeCallback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldUpdateProject() throws Exception {
        prepareWizard(UPDATE);

        wizard.complete(completeCallback);

        verify(projectServiceClient).updateProject(eq(PROJECT_NAME), eq(newProject), callbackCaptor.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, mock(ProjectDescriptor.class));

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldInvokeCallbackWhenUpdatingFailure() throws Exception {
        prepareWizard(UPDATE);
        when(dtoFactory.createDtoFromJson(anyString(), any(Class.class))).thenReturn(mock(ServiceError.class));

        wizard.complete(completeCallback);

        verify(projectServiceClient).updateProject(eq(PROJECT_NAME), eq(newProject), callbackCaptor.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));

        verify(completeCallback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldRenameProjectBeforeUpdating() throws Exception {
        prepareWizard(UPDATE);
        String changedName = PROJECT_NAME + "1";
        when(newProject.getName()).thenReturn(changedName);

        wizard.complete(completeCallback);

        // should rename
        verify(projectServiceClient).rename(eq(PROJECT_NAME), eq(changedName), anyString(), callbackCaptorForVoid.capture());

        AsyncRequestCallback<Void> voidCallback = callbackCaptorForVoid.getValue();
        GwtReflectionUtils.callOnSuccess(voidCallback,(Void)null);

        // should update
        verify(projectServiceClient).updateProject(eq(changedName), eq(newProject), callbackCaptor.capture());

        AsyncRequestCallback<ProjectDescriptor> callback = callbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(callback, mock(ProjectDescriptor.class));

        verify(eventBus).fireEvent(Matchers.<Event<Object>>anyObject());
        verify(completeCallback).onCompleted();
    }

    @Test
    public void shouldNotUpdateProjectWhenRenameFailed() throws Exception {
        prepareWizard(UPDATE);
        String changedName = PROJECT_NAME + "1";
        when(newProject.getName()).thenReturn(changedName);
        when(dtoFactory.createDtoFromJson(anyString(), any(Class.class))).thenReturn(mock(ServiceError.class));

        wizard.complete(completeCallback);

        verify(projectServiceClient).rename(eq(PROJECT_NAME), eq(changedName), anyString(), callbackCaptorForVoid.capture());

        AsyncRequestCallback<Void> callback = callbackCaptorForVoid.getValue();
        GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));

        verify(projectServiceClient, never()).updateProject(anyString(),
                                                            Matchers.<ProjectUpdate>anyObject(),
                                                            Matchers.<AsyncRequestCallback<ProjectDescriptor>>anyObject());
        verify(completeCallback).onFailure(Matchers.<Throwable>anyObject());
    }

    private void prepareWizard(ProjectWizardMode mode) {
        wizard = new ProjectWizard(importProject,
                                   mode,
                                   PROJECT_NAME,
                                   coreLocalizationConstant,
                                   projectServiceClient,
                                   dtoUnmarshallerFactory,
                                   dtoFactory,
                                   dialogFactory,
                                   eventBus,
                                   appContext);
    }
}