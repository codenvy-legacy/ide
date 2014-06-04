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
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for changing variables value.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ChangeValuePresenter implements ChangeValueView.ActionDelegate {
    private final DtoFactory                      dtoFactory;
    private       ChangeValueView                 view;
    /** Variable to change its value. */
    private       Variable                        variable;
    /** Connected debugger information. */
    private       DebuggerInfo                    debuggerInfo;
    private       DebuggerClientService           service;
    private       JavaRuntimeLocalizationConstant constant;
    private       NotificationManager             notificationManager;
    private       AsyncCallback<String>           callback;

    /** Create presenter. */
    @Inject
    public ChangeValuePresenter(ChangeValueView view, DebuggerClientService service, JavaRuntimeLocalizationConstant constant,
                                NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog(@NotNull DebuggerInfo debuggerInfo, @NotNull Variable variable, @NotNull AsyncCallback<String> callback) {
        this.debuggerInfo = debuggerInfo;
        this.variable = variable;
        this.callback = callback;

        view.setValueTitle(constant.changeValueViewExpressionFieldTitle(variable.getName()));
        view.setValue(variable.getValue());
        view.focusInValueField();
        view.selectAllText();
        view.setEnableChangeButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onChangeClicked() {
        final String newValue = view.getValue();
        UpdateVariableRequest updateVariableRequest = dtoFactory.createDto(UpdateVariableRequest.class);
        updateVariableRequest.setVariablePath(variable.getVariablePath());
        updateVariableRequest.setExpression(newValue);

        service.setValue(debuggerInfo.getId(), updateVariableRequest, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                callback.onSuccess(newValue);
            }

            @Override
            protected void onFailure(Throwable exception) {
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                callback.onFailure(exception);
            }
        });

        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onVariableValueChanged() {
        final String value = view.getValue();
        boolean isExpressionFieldNotEmpty = !value.trim().isEmpty();
        view.setEnableChangeButton(isExpressionFieldNotEmpty);
    }
}