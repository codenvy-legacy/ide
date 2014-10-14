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
package com.codenvy.ide.extension.runner.client.run.customrun;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.app.CurrentUser;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.BaseTest;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CustomRunPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomRunTest extends BaseTest {
    private static String RUNNER_NAME = "my_runner";
    @Mock
    private CustomRunView        view;
    @Mock
    private DtoFactory           dtoFactory;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private CustomRunPresenter   presenter;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        RunnerDescriptor runnerDescriptor = mock(RunnerDescriptor.class);
        when(runnerDescriptor.getName()).thenReturn(RUNNER_NAME);
        when(currentProject.getRunner()).thenReturn(RUNNER_NAME);
    }

    @Test
    public void shouldShowDialog() throws Exception {
        final ResourcesDescriptor resourcesDescriptor = mock(ResourcesDescriptor.class);
        ProfileDescriptor profileDescriptor = mock(ProfileDescriptor.class);
        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        RunnersDescriptor runnersDescriptor = mock(RunnersDescriptor.class);
        when(currentProject.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getRunners()).thenReturn(runnersDescriptor);
        when(runnersDescriptor.getDefault()).thenReturn("Tomcat7");
        when(appContext.getCurrentUser()).thenReturn(new CurrentUser(profileDescriptor));
        when(resourcesDescriptor.getTotalMemory()).thenReturn("512");
        when(resourcesDescriptor.getUsedMemory()).thenReturn("256");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<RunnerEnvironmentTree> callback = (AsyncRequestCallback<RunnerEnvironmentTree>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mock(RunnerEnvironmentTree.class));
                return callback;
            }
        }).when(projectServiceClient).getRunnerEnvironments(anyString(), Matchers.<AsyncRequestCallback<RunnerEnvironmentTree>>anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<RunnerEnvironmentTree> callback = (AsyncRequestCallback<RunnerEnvironmentTree>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mock(RunnerEnvironmentTree.class));
                return callback;
            }
        }).when(runnerServiceClient).getRunners(Matchers.<AsyncRequestCallback<RunnerEnvironmentTree>>anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<RunnerEnvironmentTree> callback = (AsyncRequestCallback<RunnerEnvironmentTree>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, resourcesDescriptor);
                return callback;
            }
        }).when(runnerServiceClient).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());

        presenter.showDialog();

        verify(projectServiceClient).getRunnerEnvironments(anyString(), Matchers.<AsyncRequestCallback<RunnerEnvironmentTree>>anyObject());
        verify(view).addRunner(Matchers.<RunnerEnvironmentTree>anyObject());
        verify(runnerServiceClient).getRunners(Matchers.<AsyncRequestCallback<RunnerEnvironmentTree>>anyObject());
        verify(runnerServiceClient).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());
        verify(view).setRunnerMemorySize(anyString());
        verify(view).setTotalMemorySize(anyString());
        verify(view).setAvailableMemorySize(anyString());
        verify(view).showDialog();
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void onRunClickedAndRunnerMemoryCorrect() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn("128");
        when(view.getTotalMemorySize()).thenReturn("512");
        when(view.getAvailableMemorySize()).thenReturn("256");
        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);

        presenter.onEnvironmentSelected(mock(RunnerEnvironment.class));
        presenter.onRunClicked();

        verify(view).close();
        verify(dtoFactory).createDto(eq(RunOptions.class));
        verify(view, times(2)).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(runOptions).setMemorySize(eq(128));
        verify(runController).runActiveProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndTotalLessThanCustomRunMemory() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn("1024");
        when(view.getTotalMemorySize()).thenReturn("512");
        when(view.getAvailableMemorySize()).thenReturn("256");

        presenter.onEnvironmentSelected(mock(RunnerEnvironment.class));
        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runController, never()).runActiveProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndCustomRunMemoryIncorrect() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn("not int");

        presenter.onEnvironmentSelected(mock(RunnerEnvironment.class));
        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runController, never()).runActiveProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndCustomRunMemoryNotMultipleOf128() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn("255");

        presenter.onEnvironmentSelected(mock(RunnerEnvironment.class));
        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runController, never()).runActiveProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndAvailableLessThanCustomRunMemory() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn("512");
        when(view.getTotalMemorySize()).thenReturn("512");
        when(view.getAvailableMemorySize()).thenReturn("256");

        presenter.onEnvironmentSelected(mock(RunnerEnvironment.class));
        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runController, never()).runActiveProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
