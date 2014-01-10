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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for evaluate expression.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EvaluateExpressionPresenter implements EvaluateExpressionView.ActionDelegate {
    private EvaluateExpressionView          view;
    private DebuggerClientService           service;
    private DebuggerInfo                    debuggerInfo;
    private JavaRuntimeLocalizationConstant constant;
    private NotificationManager             notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param constant
     * @param notificationManager
     */
    @Inject
    public EvaluateExpressionPresenter(EvaluateExpressionView view, DebuggerClientService service, JavaRuntimeLocalizationConstant constant,
                                       NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog(@NotNull DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;

        view.setExpression("");
        view.focusInExpressionField();
        view.setEnableEvaluateButton(false);

        view.showDialog();
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
        try {
            service.evaluateExpression(debuggerInfo.getId(), view.getExpression(),
                                       new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                           @Override
                                           protected void onSuccess(String result) {
                                               view.setResult(result);
                                               view.setEnableEvaluateButton(true);
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               String errorMessage = constant.evaluateExpressionFailed(exception.getMessage());
                                               view.setResult(errorMessage);
                                               view.setEnableEvaluateButton(true);
                                           }
                                       });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            view.setEnableEvaluateButton(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onValueExpressionChanged() {
        String expression = view.getExpression();
        boolean isExpressionFieldNotEmpty = !expression.trim().isEmpty();
        view.setEnableEvaluateButton(isExpressionFieldNotEmpty);
    }
}