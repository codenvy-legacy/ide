/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CustomRunPresenter} functionality
 *
 * @author Artem Zatsarynnyy
 */
public class RunOptionsTest extends BaseTest {
    private static String RUNNER_NAME = "my_runner";
    @Mock
    private CustomRunView      view;
    @Mock
    private RunnerController   runnerController;
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

        when(activeProject.getAttributeValue(anyString())).thenReturn(RUNNER_NAME);
    }

    @Test
    public void shouldShowDialog() throws Exception {
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

        presenter.showDialog();

        verify(service).getRunners(Matchers.<AsyncRequestCallback<Array<RunnerDescriptor>>>anyObject());
        verify(view).setEnvironments(Matchers.<Array<RunnerEnvironment>>anyObject());
        verify(view, timeout(1)).showDialog();
    }

    @Test
    public void shouldNotShowDialog() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[0];
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
    public void shouldRunProject() throws Exception {
        presenter.onRunClicked();

        verify(view).close();
        verify(runnerController).runActiveProject((RunnerEnvironment)anyObject());
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
