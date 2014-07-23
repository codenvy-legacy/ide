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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerView;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionPresenter;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolver;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.Location;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private static final String VM_NAME    = "vm_name";
    private static final String VM_VERSION = "vm_version";
    private static final String MIME_TYPE  = "application/java";
    @Mock
    private DebuggerView                 view;
    @Mock
    private EvaluateExpressionPresenter  evaluateExpressionPresenter;
    @Mock
    private ChangeValuePresenter         changeValuePresenter;
    @InjectMocks
    private DebuggerPresenter            presenter;
    @Mock
    private ApplicationProcessDescriptor applicationProcessDescriptor;
    @Mock
    private RunnerController             runnerController;
    @Mock
    private BreakpointGutterManager      gutterManager;
    @Mock
    private ItemReference                file;
    @Mock
    private FqnResolverFactory           resolverFactory;
    @Mock
    private AsyncCallback<Breakpoint>    asyncCallbackBreakpoint;
    @Mock
    private Project                      project;
    @Mock
    private AsyncCallback<Void>          asyncCallbackVoid;

    @Before
    public void setUp() {
        super.setUp();
        when(applicationProcessDescriptor.getDebugHost()).thenReturn(DEBUG_HOST);
        when(applicationProcessDescriptor.getDebugPort()).thenReturn(DEBUG_PORT);
        when(file.getMediaType()).thenReturn(MIME_TYPE);
        when(dtoFactory.createDto(Location.class)).thenReturn(mock(Location.class));
        when(dtoFactory.createDto(BreakPoint.class)).thenReturn(mock(BreakPoint.class));
        when(resolverFactory.getResolver(anyString())).thenReturn(mock(FqnResolver.class));
    }

    @Test
    public void testConnectDebuggerRequestIsSuccessful() throws Exception {
        final DebuggerInfo debuggerInfoMock = mock(DebuggerInfo.class);
        when(debuggerInfoMock.getVmName()).thenReturn(VM_NAME);
        when(debuggerInfoMock.getVmVersion()).thenReturn(VM_VERSION);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<DebuggerInfo> callback = (AsyncRequestCallback<DebuggerInfo>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, debuggerInfoMock);
                return callback;
            }
        }).when(service).connect(anyString(), anyInt(), (AsyncRequestCallback<DebuggerInfo>)anyObject());

        presenter.attachDebugger(applicationProcessDescriptor);

        verify(service).connect(eq(DEBUG_HOST), eq(DEBUG_PORT), (AsyncRequestCallback<DebuggerInfo>)anyObject());
        verifySetEnableButtons(DISABLE_BUTTON);
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(view).setEnableRemoveAllBreakpointsButton(!DISABLE_BUTTON);
        verify(view).setEnableDisconnectButton(!DISABLE_BUTTON);
        verify(workspaceAgent).openPart(presenter, PartStackType.INFORMATION);
    }

    @Test
    public void testConnectDebuggerRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<DebuggerInfo> callback = (AsyncRequestCallback<DebuggerInfo>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).connect(anyString(), anyInt(), (AsyncRequestCallback<DebuggerInfo>)anyObject());

        presenter.attachDebugger(applicationProcessDescriptor);

        verify(service).connect(eq(DEBUG_HOST), eq(DEBUG_PORT), (AsyncRequestCallback<DebuggerInfo>)anyObject());
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

        verify(runnerController).stopActiveProject(false);
        verify(gutterManager).unmarkCurrentBreakpoint();
        verify(gutterManager).removeAllBreakPoints();
        verify(view).setEnableRemoveAllBreakpointsButton(DISABLE_BUTTON);
        verify(view).setEnableDisconnectButton(DISABLE_BUTTON);
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
    public void testResumeRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).resume(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onResumeButtonClicked();

        verifySetEnableButtons(DISABLE_BUTTON);
        verify(service).resume(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakpoint();
    }

    @Test
    public void testResumeRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).resume(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.onResumeButtonClicked();

        verify(service).resume(anyString(), (AsyncRequestCallback<Void>)anyObject());
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
        when(view.resetStepIntoButton(false)).thenReturn(true);

        presenter.onStepIntoButtonClicked();

        verify(service).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakpoint();
    }

    @Test
    public void testStepIntoRequestIfKeyup() throws Exception {
        when(view.resetStepIntoButton(false)).thenReturn(false);

        presenter.onStepIntoButtonClicked();

        verify(service, never()).stepInto(anyString(), (AsyncRequestCallback<Void>)anyObject());
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
        when(view.resetStepIntoButton(false)).thenReturn(true);

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
        when(view.resetStepOverButton(false)).thenReturn(true);

        presenter.onStepOverButtonClicked();

        verify(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakpoint();
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
        when(view.resetStepOverButton(false)).thenReturn(true);

        presenter.onStepOverButtonClicked();

        verify(service).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepOverRequestIfKeyup() throws Exception {
        when(view.resetStepOverButton(false)).thenReturn(false);

        presenter.onStepOverButtonClicked();

        verify(service, never()).stepOver(anyString(), (AsyncRequestCallback<Void>)anyObject());
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
        when(view.resetStepReturnButton(false)).thenReturn(true);

        presenter.onStepReturnButtonClicked();

        verify(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(view).setVariables(anyListOf(Variable.class));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(gutterManager).unmarkCurrentBreakpoint();
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
        when(view.resetStepReturnButton(false)).thenReturn(true);

        presenter.onStepReturnButtonClicked();

        verify(service).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testStepReturnRequestIfKeyup() throws Exception {
        when(view.resetStepReturnButton(false)).thenReturn(false);

        presenter.onStepReturnButtonClicked();

        verify(service, never()).stepReturn(anyString(), (AsyncRequestCallback<Void>)anyObject());
    }

    @Test
    public void testAddBreakpointRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).addBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        presenter.addBreakpoint(file, anyInt(), asyncCallbackBreakpoint);

        verify(service).addBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        verify(asyncCallbackBreakpoint).onSuccess((Breakpoint)anyObject());
    }

    @Test
    public void testAddBreakpointRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).addBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        presenter.addBreakpoint(file, anyInt(), asyncCallbackBreakpoint);

        verify(service).addBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        verify(asyncCallbackBreakpoint).onFailure((Throwable)anyObject());
    }

    @Test
    public void testRemoveBreakpointRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).deleteBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        presenter.deleteBreakpoint(file, anyInt(), asyncCallbackVoid);

        verify(service).deleteBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        verify(asyncCallbackVoid).onSuccess((Void)anyObject());
    }

    @Test
    public void testRemoveBreakpointRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).deleteBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());

        presenter.deleteBreakpoint(file, anyInt(), asyncCallbackVoid);

        verify(service).deleteBreakpoint(anyString(), (BreakPoint)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        verify(asyncCallbackVoid).onFailure((Throwable)anyObject());
    }

    @Test
    public void shouldOpenChangeVariableValueDialog() throws Exception {
        presenter.onSelectedVariableElement(mock(Variable.class));
        presenter.onChangeValueButtonClicked();

        verify(changeValuePresenter).showDialog((DebuggerInfo)anyObject(), (Variable)anyObject(), (AsyncCallback<String>)anyObject());
    }

    @Test
    public void shouldOpenEvaluateExpressionDialog() throws Exception {
        presenter.onEvaluateExpressionButtonClicked();

        verify(evaluateExpressionPresenter).showDialog((DebuggerInfo)anyObject());
    }

    protected void verifySetEnableButtons(boolean enabled) {
        verify(view).setEnableResumeButton(eq(enabled));
        verify(view).setEnableStepIntoButton(eq(enabled));
        verify(view).setEnableStepOverButton(eq(enabled));
        verify(view).setEnableStepReturnButton(eq(enabled));
        verify(view).setEnableEvaluateExpressionButtonEnable(eq(enabled));
    }
}
