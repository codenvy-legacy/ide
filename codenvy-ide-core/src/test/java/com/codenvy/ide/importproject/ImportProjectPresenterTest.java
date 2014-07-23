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
package com.codenvy.ide.importproject;

import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.Constants;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.ProjectDescription;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link com.codenvy.ide.importproject.ImportProjectPresenter} functionality.
 *
 * @author Roman Nikitenko.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportProjectPresenterTest {

    public static final String PROJECT_Name = "ide";
    public static final String IMPORTER     = "git";
    public static final String URI          = "https://github.com/codenvy/hello.git";

    @Mock
    private   ImportProjectView         view;
    @Mock
    protected Project                   project;
    @Mock
    private   CoreLocalizationConstant  locale;
    @Mock
    private   DtoFactory                dtoFactory;
    @Mock
    private   ProjectDescriptor         projectDescriptor;
    @Mock
    private   ResourceProvider          resourceProvider;
    @Mock
    private   NotificationManager       notificationManager;
    @Mock
    private   ProjectServiceClient      projectServiceClient;
    @Mock
    private   NewProjectWizardPresenter projectWizardPresenter;
    @Mock
    private   ImportSourceDescriptor    importSourceDescriptor;

    @Mock
    private ProjectImportersServiceClient projectImportersServiceClient;

    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @InjectMocks
    private ImportProjectPresenter presenter;

//    @Test
//    public void onCancelClickedShouldBeExecuted() {
//        presenter.showDialog();
//
//        presenter.onCancelClicked();
//
//        verify(view).close();
//    }
//
//    @Test
//    public void onImportClickedWhenImportProjectIsSuccessfulShouldBeExecuted() {
//        ProjectDescription projectDescription = mock(ProjectDescription.class);
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<ProjectDescriptor> callback = (AsyncRequestCallback<ProjectDescriptor>)arguments[2];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, projectDescriptor);
//                return callback;
//            }
//        }).when(projectServiceClient).importProject(anyString(), (ImportSourceDescriptor)anyObject(),
//                                                    (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
//                callback.onSuccess(project);
//                return callback;
//            }
//        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
//        view.showDialog();
//        when(view.getUri()).thenReturn(URI);
//        when(view.getImporter()).thenReturn(IMPORTER);
//        when(view.getProjectName()).thenReturn(PROJECT_Name);
//        when(dtoFactory.createDto(ImportSourceDescriptor.class)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withType(IMPORTER)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withLocation(URI)).thenReturn(importSourceDescriptor);
//        when(locale.importProjectMessageSuccess()).thenReturn("Success!");
//        when(project.getDescription()).thenReturn(projectDescription);
//        when(projectDescription.getProjectTypeId()).thenReturn(Constants.NAMELESS_ID);
//
//        presenter.onImportClicked();
//
//        verify(view).getUri();
//        verify(view).getImporter();
//        verify(view).getProjectName();
//        verify(view).close();
//        verify(dtoFactory).createDto(ImportSourceDescriptor.class);
//        verify(importSourceDescriptor).withType(anyString());
//        verify(importSourceDescriptor).withLocation(anyString());
//        verify(projectServiceClient)
//                .importProject(anyString(), (ImportSourceDescriptor)anyObject(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        verify(resourceProvider).getProject(eq(PROJECT_Name), (AsyncCallback<Project>)anyObject());
//        verify(locale).importProjectMessageSuccess();
//        verify(notificationManager).showNotification((Notification)anyObject());
//        verify(projectWizardPresenter).show((com.codenvy.ide.api.ui.wizard.WizardContext)anyObject());
//    }
//
//    @Test
//    public void onImportClickedWhenImportProjectIsFailedShouldBeExecuted() {
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<ProjectDescriptor> callback = (AsyncRequestCallback<ProjectDescriptor>)arguments[2];
//                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
//                onFailure.invoke(callback, mock(Throwable.class));
//                return callback;
//            }
//        }).when(projectServiceClient).importProject(anyString(), (ImportSourceDescriptor)anyObject(),
//                                                    (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
//                callback.onSuccess(project);
//                return callback;
//            }
//        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
//        view.showDialog();
//        when(view.getUri()).thenReturn(URI);
//        when(view.getImporter()).thenReturn(IMPORTER);
//        when(view.getProjectName()).thenReturn(PROJECT_Name);
//        when(dtoFactory.createDto(ImportSourceDescriptor.class)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withType(IMPORTER)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withLocation(URI)).thenReturn(importSourceDescriptor);
//
//        presenter.onImportClicked();
//
//        verify(view).getUri();
//        verify(view).getImporter();
//        verify(view).getProjectName();
//        verify(view).close();
//        verify(dtoFactory).createDto(ImportSourceDescriptor.class);
//        verify(importSourceDescriptor).withType(anyString());
//        verify(importSourceDescriptor).withLocation(anyString());
//        verify(projectServiceClient)
//                .importProject(anyString(), (ImportSourceDescriptor)anyObject(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        verify(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
//        verify(projectWizardPresenter, never()).show((com.codenvy.ide.api.ui.wizard.WizardContext)anyObject());
//        verify(notificationManager).showNotification((Notification)anyObject());
//        verify(resourceProvider).getProject(eq(PROJECT_Name), (AsyncCallback<Project>)anyObject());
//        verify(resourceProvider).delete((Project)anyObject(), (AsyncCallback<String>)anyObject());
//    }
//
//    @Test
//    public void onImportClickedWhenImportProjectIsSuccessfulButGetProjectIsFailedShouldBeExecuted() {
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncRequestCallback<ProjectDescriptor> callback = (AsyncRequestCallback<ProjectDescriptor>)arguments[2];
//                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
//                onSuccess.invoke(callback, projectDescriptor);
//                return callback;
//            }
//        }).when(projectServiceClient).importProject(anyString(), (ImportSourceDescriptor)anyObject(),
//                                                    (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] arguments = invocation.getArguments();
//                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
//                callback.onFailure(mock(Throwable.class));
//                return callback;
//            }
//        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());
//        view.showDialog();
//        when(view.getUri()).thenReturn(URI);
//        when(view.getImporter()).thenReturn(IMPORTER);
//        when(view.getProjectName()).thenReturn(PROJECT_Name);
//        when(dtoFactory.createDto(ImportSourceDescriptor.class)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withType(IMPORTER)).thenReturn(importSourceDescriptor);
//        when(importSourceDescriptor.withLocation(URI)).thenReturn(importSourceDescriptor);
//
//        presenter.onImportClicked();
//
//        verify(view).getUri();
//        verify(view).getImporter();
//        verify(view).getProjectName();
//        verify(view).close();
//        verify(dtoFactory).createDto(ImportSourceDescriptor.class);
//        verify(importSourceDescriptor).withType(anyString());
//        verify(importSourceDescriptor).withLocation(anyString());
//        verify(projectServiceClient)
//                .importProject(anyString(), (ImportSourceDescriptor)anyObject(), (AsyncRequestCallback<ProjectDescriptor>)anyObject());
//        verify(resourceProvider).getProject(eq(PROJECT_Name), (AsyncCallback<Project>)anyObject());
//        verify(projectWizardPresenter, never()).show((com.codenvy.ide.api.ui.wizard.WizardContext)anyObject());
//        verify(notificationManager).showNotification((Notification)anyObject());
//    }
//
//    @Test
//    public void onValueChangedWhenProjectNameIsEmptyShouldBeExecuted() {
//        when(view.getProjectName()).thenReturn("");
//        when(view.getUri()).thenReturn(URI);
//
//        presenter.onValueChanged();
//
//        verify(view).getUri();
//        verify(view).getProjectName();
//        verify(view).setProjectName(anyString());
//        verify(view).setEnabledImportButton(eq(true));
//    }
//
//    @Test
//    public void onValueChangedWhenProjectNameIsNotEmptyShouldBeExecuted() {
//        when(view.getProjectName()).thenReturn(PROJECT_Name);
//        when(view.getUri()).thenReturn(URI);
//
//        presenter.onValueChanged();
//
//        verify(view).getUri();
//        verify(view).getProjectName();
//        verify(view, never()).setProjectName(anyString());
//        verify(view).setEnabledImportButton(eq(true));
//    }
}
