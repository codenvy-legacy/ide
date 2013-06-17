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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for evaluate expression.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EvaluateExpressionPresenter.java May 7, 2012 13:29:01 PM azatsarynnyy $
 */
@Singleton
public class EvaluateExpressionPresenter implements EvaluateExpressionView.ActionDelegate {
    private EvaluateExpressionView          view;
    private DebuggerClientService           service;
    private DebuggerInfo                    debuggerInfo;
    private JavaRuntimeLocalizationConstant constant;
    private EventBus                        eventBus;
    private ConsolePart                     console;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param constant
     * @param eventBus
     * @param console
     */
    @Inject
    protected EvaluateExpressionPresenter(EvaluateExpressionView view, DebuggerClientService service,
                                          JavaRuntimeLocalizationConstant constant, EventBus eventBus, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.eventBus = eventBus;
        this.console = console;
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

        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

        try {
            service.evaluateExpression(debuggerInfo.getId(),
                                       view.getExpression(),
                                       new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                                           @Override
                                           protected void onSuccess(StringBuilder result) {
                                               view.setResult(result.toString());
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
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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