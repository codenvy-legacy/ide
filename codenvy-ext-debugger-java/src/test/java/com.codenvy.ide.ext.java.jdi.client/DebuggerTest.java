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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerView;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.extension.runner.client.RunnerController;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link DebuggerPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class DebuggerTest extends BaseTest {

    private static final String DEBUG_HOST = "localhost";
    private static final int    DEBUG_PORT = 8000;
    private static final String TEST_JSON  = "test_json";
    private static final String VM_NAME    = "vm_name";
    private static final String VM_VERSION = "vm_version";
    @Mock
    private DebuggerView                 view;
    @InjectMocks
    private DebuggerPresenter            presenter;
    @Mock
    private ApplicationProcessDescriptor applicationProcessDescriptor;
    @Mock
    private RunnerController             runnerController;
    @Mock
    private BreakpointGutterManager      gutterManager;

    @Before
    public void setUp() {
        super.setUp();
        when(applicationProcessDescriptor.getDebugHost()).thenReturn(DEBUG_HOST);
        when(applicationProcessDescriptor.getDebugPort()).thenReturn(DEBUG_PORT);

        DebuggerInfo debuggerInfoMock = mock(DebuggerInfo.class);
        when(dtoFactory.createDtoFromJson(TEST_JSON, DebuggerInfo.class)).thenReturn(debuggerInfoMock);
        when(debuggerInfoMock.getVmName()).thenReturn(VM_NAME);
        when(debuggerInfoMock.getVmVersion()).thenReturn(VM_VERSION);
    }

    @Test
    public void testConnectDebuggerRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, TEST_JSON);
                return callback;
            }
        }).when(service).connect(anyString(), anyInt(), (AsyncRequestCallback<String>)anyObject());

        presenter.attachDebugger(applicationProcessDescriptor);

        verify(service).connect(eq(DEBUG_HOST), eq(DEBUG_PORT), (AsyncRequestCallback<String>)anyObject());
        verify(console).print(constants.debuggerConnected(anyString()));

        verifySetEnableButtons(DISABLE_BUTTON);
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));

        verify(workspaceAgent).openPart(presenter, PartStackType.INFORMATION);
    }

    @Test
    public void testConnectDebuggerRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).connect(anyString(), anyInt(), (AsyncRequestCallback<String>)anyObject());

        presenter.attachDebugger(applicationProcessDescriptor);

        verify(service).connect(eq(DEBUG_HOST), eq(DEBUG_PORT), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testConnectDebuggerRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).connect(anyString(), anyInt(),
                                                              (AsyncRequestCallback<String>)anyObject());

        presenter.attachDebugger(applicationProcessDescriptor);

        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testDisconnectDebuggerRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onDisconnectButtonClicked();

        verify(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());

        verifySetEnableButtons(DISABLE_BUTTON);

        verify(runnerController).stopActiveProject();
        verify(gutterManager).unmarkCurrentBreakPoint();
        verify(gutterManager).removeAllBreakPoints();
        verify(console).print(constants.debuggerDisconnected(anyString()));
        verify(workspaceAgent).hidePart(presenter);
        verify(workspaceAgent).removePart(presenter);
    }

    @Test
    public void testDisconnectDebuggerRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onDisconnectButtonClicked();

        verify(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testDisconnectDebuggerRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onDisconnectButtonClicked();

        verify(service).disconnect(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepIntoRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepIntoButtonClicked();

        verifySetEnableButtons(DISABLE_BUTTON);
        verify(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakPoint();
    }

    @Test
    public void testStepIntoRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepIntoButtonClicked();

        verify(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepIntoRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepIntoButtonClicked();

        verify(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepOverRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepOverButtonClicked();

        verifySetEnableButtons(DISABLE_BUTTON);
        verify(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakPoint();
    }

    @Test
    public void testStepOverRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepOverButtonClicked();

        verify(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepOverRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepOverButtonClicked();

        verify(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepReturnRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepReturnButtonClicked();

        verifySetEnableButtons(DISABLE_BUTTON);
        verify(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakPoint();
    }

    @Test
    public void testStepReturnRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepReturnButtonClicked();

        verify(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepReturnRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onStepReturnButtonClicked();

        verify(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    protected void verifySetEnableButtons(boolean enabled) {
        verify(view).setEnableResumeButton(eq(enabled));
        verify(view).setEnableStepIntoButton(eq(enabled));
        verify(view).setEnableStepOverButton(eq(enabled));
        verify(view).setEnableStepReturnButton(eq(enabled));
        verify(view).setEnableEvaluateExpressionButtonEnable(eq(enabled));
    }
}
