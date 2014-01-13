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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValueView;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ext.java.jdi.shared.VariablePath;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ChangeValuePresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class ChangeVariableValueTest extends BaseTest {
    private static final String VAR_VALUE   = "var_value";
    private static final String VAR_NAME    = "var_name";
    private static final String EMPTY_VALUE = "";
    @Mock
    private ChangeValueView       view;
    @InjectMocks
    private ChangeValuePresenter  presenter;
    @Mock
    private Variable              var;
    @Mock
    private VariablePath          varPath;
    @Mock
    private AsyncCallback<String> asyncCallback;

    @Before
    public void setUp() {
        super.setUp();
        when(var.getName()).thenReturn(VAR_NAME);
        when(var.getValue()).thenReturn(VAR_VALUE);
        when(var.getVariablePath()).thenReturn(varPath);
        when(dtoFactory.createDto(UpdateVariableRequest.class)).thenReturn(mock(UpdateVariableRequest.class));
    }

    @Test
    public void shouldShowDialog() throws Exception {
        presenter.showDialog(debuggerInfo, var, asyncCallback);

        verify(view).setValueTitle(constants.changeValueViewExpressionFieldTitle(VAR_NAME));
        verify(view).setValue(VAR_VALUE);
        verify(view).focusInValueField();
        verify(view).selectAllText();
        verify(view).setEnableChangeButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    public void shouldCloseDialogOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void shouldDisableChangeButtonIfNoValue() throws Exception {
        when(view.getValue()).thenReturn(EMPTY_VALUE);

        presenter.onVariableValueChanged();

        verify(view).setEnableChangeButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void shouldEnableChangeButtonIfValueNotEmpty() throws Exception {
        when(view.getValue()).thenReturn(VAR_VALUE);

        presenter.onVariableValueChanged();

        verify(view).setEnableChangeButton(eq(!DISABLE_BUTTON));
    }

    @Test
    public void testChangeValueRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).setValue(anyString(), (UpdateVariableRequest)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        when(view.getValue()).thenReturn(VAR_VALUE);

        presenter.showDialog(debuggerInfo, var, asyncCallback);
        presenter.onChangeClicked();

        verify(asyncCallback).onSuccess(eq(VAR_VALUE));
        verify(view).close();
    }

    @Test
    public void testChangeValueRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).setValue(anyString(), (UpdateVariableRequest)anyObject(), (AsyncRequestCallback<Void>)anyObject());
        when(view.getValue()).thenReturn(VAR_VALUE);

        presenter.showDialog(debuggerInfo, var, asyncCallback);
        presenter.onChangeClicked();

        verify(asyncCallback).onFailure((Throwable)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testChangeValueRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).setValue(anyString(), (UpdateVariableRequest)anyObject(),
                                                               (AsyncRequestCallback<Void>)anyObject());

        presenter.showDialog(debuggerInfo, var, asyncCallback);
        presenter.onChangeClicked();

        verify(asyncCallback).onFailure((RequestException)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }
}
