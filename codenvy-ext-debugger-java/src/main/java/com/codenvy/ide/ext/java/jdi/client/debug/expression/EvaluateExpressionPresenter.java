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