/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * Presenter for evaluating an expression.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EvaluateExpressionPresenter implements EvaluateExpressionView.ActionDelegate {
    private EvaluateExpressionView          view;
    private DebuggerClientService           service;
    private DebuggerInfo                    debuggerInfo;
    private JavaRuntimeLocalizationConstant constant;

    /** Create presenter. */
    @Inject
    public EvaluateExpressionPresenter(EvaluateExpressionView view, DebuggerClientService service,
                                       JavaRuntimeLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
    }

    /** Show dialog. */
    public void showDialog(@NotNull DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
        view.setExpression("");
        view.setResult("");
        view.setEnableEvaluateButton(false);
        view.showDialog();
        view.focusInExpressionField();
    }

    /** Close dialog. */
    public void closeDialog() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onEvaluateClicked() {
        view.setEnableEvaluateButton(false);
        service.evaluateExpression(debuggerInfo.getId(), view.getExpression(),
                                   new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           view.setResult(result);
                                           view.setEnableEvaluateButton(true);
                                       }

                                       @Override
                                       protected void onFailure(Throwable exception) {
                                           view.setResult(constant.evaluateExpressionFailed(exception.getMessage()));
                                           view.setEnableEvaluateButton(true);
                                       }
                                   });
    }

    /** {@inheritDoc} */
    @Override
    public void onExpressionValueChanged() {
        final String expression = view.getExpression();
        boolean isExpressionFieldNotEmpty = !expression.trim().isEmpty();
        view.setEnableEvaluateButton(isExpressionFieldNotEmpty);
    }
}