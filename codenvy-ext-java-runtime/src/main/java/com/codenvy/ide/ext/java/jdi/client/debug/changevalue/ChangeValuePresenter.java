/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for change value in debug process.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValuePresenter.java Apr 28, 2012 9:47:01 AM azatsarynnyy $
 */
@Singleton
public class ChangeValuePresenter implements ChangeValueView.ActionDelegate {
    private ChangeValueView                 view;
    /** Variable whose value need to change. */
    private Variable                        variable;
    /** Connected debugger information. */
    private DebuggerInfo                    debuggerInfo;
    private DebuggerClientService           service;
    private EventBus                        eventBus;
    private ConsolePart                     console;
    private JavaRuntimeLocalizationConstant constant;
    private AsyncCallback<String>           callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param console
     * @param constant
     */
    @Inject
    protected ChangeValuePresenter(ChangeValueView view, DebuggerClientService service, EventBus eventBus, ConsolePart console,
                                   JavaRuntimeLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
    }

    /**
     * Show dialog.
     *
     * @param debuggerInfo
     * @param variable
     * @param callback
     */
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
        DtoClientImpls.UpdateVariableRequestImpl request = DtoClientImpls.UpdateVariableRequestImpl.make();
        request.setVariablePath(variable.getVariablePath());
        request.setExpression(newValue);

        try {
            service.setValue(debuggerInfo.getId(), request, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    callback.onSuccess(newValue);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                    callback.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            callback.onFailure(e);
        }

        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String value = view.getValue();
        boolean isExpressionFieldNotEmpty = !value.trim().isEmpty();
        view.setEnableChangeButton(isExpressionFieldNotEmpty);
    }
}