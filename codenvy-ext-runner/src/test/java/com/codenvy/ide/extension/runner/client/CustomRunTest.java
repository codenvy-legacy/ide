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

import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.app.CurrentUser;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.run.CustomRunPresenter;
import com.codenvy.ide.extension.runner.client.run.CustomRunView;
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
 * Testing {@link CustomRunPresenter} functionality
 *
 * @author Artem Zatsarynnyy
 */
public class CustomRunTest extends BaseTest {
    private static String RUNNER_NAME = "my_runner";
    @Mock
    private CustomRunView      view;
    @Mock
    private DtoFactory dtoFactory;
    @InjectMocks
    private CustomRunPresenter presenter;
    private Array<RunnerDescriptor> runnerDescriptors = Collections.createArray();

    @Before
    public void setUp() {
        super.setUp();

        runnerDescriptors.clear();
        RunnerDescriptor runnerDescriptor = mock(RunnerDescriptor.class);
        when(runnerDescriptor.getName()).thenReturn(RUNNER_NAME);
        runnerDescriptors.add(runnerDescriptor);

        when(activeProject.getRunner()).thenReturn(RUNNER_NAME);
    }

    @Test
    public void shouldNotShowDialogWhenGetResourcesFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<RunnerDescriptor>> callback = (AsyncRequestCallback<Array<RunnerDescriptor>>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, runnerDescriptors);
                return callback;
            }
        }).when(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<ResourcesDescriptor> callback = (AsyncRequestCallback<ResourcesDescriptor>)arguments[0];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());


        presenter.showDialog();

        verify(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());
        verify(service).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());
        verify(appContext).getCurrentProject();
        verify(view).setEnvironments((Array<RunnerEnvironment>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view, never()).setRunnerMemorySize(anyInt());
        verify(view, never()).setTotalMemorySize(anyInt());
        verify(view, never()).setAvailableMemorySize(anyInt());
        verify(view, never()).showDialog();
    }

    @Test
    public void shouldShowDialog() throws Exception {
        final ResourcesDescriptor resourcesDescriptor = mock(ResourcesDescriptor.class);
        ProfileDescriptor profileDescriptor = mock(ProfileDescriptor.class);
        when(appContext.getCurrentUser()).thenReturn(new CurrentUser(profileDescriptor));
        when(profileDescriptor.getPreferences()).thenReturn(null);
        when(resourcesDescriptor.getTotalMemory()).thenReturn("512");
        when(resourcesDescriptor.getUsedMemory()).thenReturn("256");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<RunnerDescriptor>> callback = (AsyncRequestCallback<Array<RunnerDescriptor>>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, runnerDescriptors);
                return callback;
            }
        }).when(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<ResourcesDescriptor> callback = (AsyncRequestCallback<ResourcesDescriptor>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, resourcesDescriptor);
                return callback;
            }
        }).when(service).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());

        presenter.showDialog();

        verify(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());
        verify(service).getResources(Matchers.<AsyncRequestCallback<ResourcesDescriptor>>anyObject());
        verify(view).setEnvironments(Matchers.<Array<RunnerEnvironment>>anyObject());
        verify(view).setRunnerMemorySize(anyInt());
        verify(view).setTotalMemorySize(anyInt());
        verify(view).setAvailableMemorySize(anyInt());
        verify(view).showDialog();
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void shouldNotShowDialogWhenGetRunnersFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<RunnerDescriptor>> callback = (AsyncRequestCallback<Array<RunnerDescriptor>>)arguments[0];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());

        presenter.showDialog();

        verify(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());
        verify(view, times(0)).showDialog();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void onRunClickedAndRunnerMemoryCorrect() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn(128);
        when(view.getTotalMemorySize()).thenReturn(512);
        when(view.getAvailableMemorySize()).thenReturn(256);
        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);

        presenter.onRunClicked();

        verify(view).close();
        verify(dtoFactory).createDto(eq(RunOptions.class));
        verify(view).getSelectedEnvironment();
        verify(view, times(2)).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(runOptions).setMemorySize(eq(128));
        verify(runnerController).runCurrentProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndTotalLessCustomRunMemory() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn(1024);
        when(view.getTotalMemorySize()).thenReturn(512);
        when(view.getAvailableMemorySize()).thenReturn(256);

        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runnerController, never()).runCurrentProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void onRunClickedAndAvailableLessCustomRunMemory() throws Exception {
        RunOptions runOptions = mock(RunOptions.class);
        when(view.getRunnerMemorySize()).thenReturn(512);
        when(view.getTotalMemorySize()).thenReturn(512);
        when(view.getAvailableMemorySize()).thenReturn(256);

        presenter.onRunClicked();

        verify(view).getRunnerMemorySize();
        verify(view).getTotalMemorySize();
        verify(view).getAvailableMemorySize();
        verify(view).showWarning(anyString());
        verify(runOptions, never()).setMemorySize(anyInt());
        verify(runOptions, never()).setSkipBuild(anyBoolean());
        verify(view, never()).close();
        verify(runnerController, never()).runCurrentProject((RunOptions)anyObject(), (ProjectRunCallback)anyObject(), anyBoolean());
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
