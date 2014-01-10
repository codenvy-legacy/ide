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

import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionPresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionView;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link EvaluateExpressionPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class EvaluateExpressionTest extends BaseTest {
    private static final String EXPRESSION = "expression";
    private static final String RESULT     = "result";
    @Mock
    private EvaluateExpressionView      view;
    private EvaluateExpressionPresenter presenter;

    @Override
    public void setUp() {
        super.setUp();
        presenter = new EvaluateExpressionPresenter(view, service, constants, notificationManager);
    }

    @Test
    public void testEvaluateExpressionRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, RESULT);
                return callback;
            }
        }).when(service).evaluateExpression(anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        when(view.getExpression()).thenReturn(EXPRESSION);

        presenter.showDialog(debuggerInfo);
        presenter.onEvaluateClicked();

        verify(view).focusInExpressionField();
        verify(view, times(2)).setEnableEvaluateButton(eq(DISABLE_BUTTON));
        verify(service).evaluateExpression(eq(DEBUGGER_ID), eq(EXPRESSION), (AsyncRequestCallback<String>)anyObject());
        verify(view, times(1)).setEnableEvaluateButton(eq(!DISABLE_BUTTON));
    }

    @Test
    public void testEvaluateExpressionRequestIsFailed() throws Exception {

    }

    @Test
    public void testEvaluateExpressionRequestExceptionHappened() throws Exception {

    }

    @Test
    public void testOnValueExpressionChanged() throws Exception {
        when(view.getExpression()).thenReturn(EXPRESSION);

        presenter.onValueExpressionChanged();

        verify(view).setEnableEvaluateButton(eq(!DISABLE_BUTTON));
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
