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
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerView;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
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
    public void shouldConnectDebugger() throws Exception {
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

        verify(view).setEnableResumeButton(eq(DISABLE_BUTTON));
        verify(view).setEnableStepIntoButton(eq(DISABLE_BUTTON));
        verify(view).setEnableStepOverButton(eq(DISABLE_BUTTON));
        verify(view).setEnableStepReturnButton(eq(DISABLE_BUTTON));
        verify(view).setEnableChangeValueButtonEnable(eq(DISABLE_BUTTON));
        verify(view).setEnableEvaluateExpressionButtonEnable(eq(DISABLE_BUTTON));

        verify(workspaceAgent).openPart(presenter, PartStackType.INFORMATION);
    }

    @Ignore
    @Test
    public void shouldDisconnectDebugger() throws Exception {
        presenter.onDisconnectButtonClicked();
    }
}
