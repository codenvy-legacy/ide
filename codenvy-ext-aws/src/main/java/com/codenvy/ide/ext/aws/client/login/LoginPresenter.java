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
package com.codenvy.ide.ext.aws.client.login;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView               view;
    private ConsolePart             console;
    private LoggedInHandler         loggedInHandler;
    private LoginCanceledHandler    loginCanceledHandler;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private BeanstalkClientService  service;

    @Inject
    protected LoginPresenter(LoginView view, ConsolePart console, EventBus eventBus, AWSLocalizationConstant constant,
                             BeanstalkClientService service) {
        this.view = view;
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;

        this.view.setDelegate(this);
    }

    public void showDialog() {
        showDialog(null, null);
    }

    public void showDialog(LoggedInHandler loggedInHandler, LoginCanceledHandler loginCanceledHandler) {
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;

        if (!view.isShown()) {
            view.setLoginResult("");
            view.showDialog();
            view.focusAccessKeyField();
        }
    }

    /** Performs any actions appropriate in response to the user having pressed the LogIn button. */
    @Override
    public void onLogInClicked() {
        final String accessKey = view.getAccessKey();
        final String secretKey = view.getSecretKey();

        try {
            service.login(accessKey, secretKey, new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    view.close();
                    if (loggedInHandler != null) {
                        loggedInHandler.onLoggedIn();
                    }
                    String msg = constant.loginSuccess();
                    console.print(msg);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    view.setLoginResult(constant.loginErrorInvalidKeyValue());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
    @Override
    public void onCancelClicked() {
        view.close();
        if (loginCanceledHandler != null) {
            loginCanceledHandler.onLoginCanceled();
        }
    }

    /** Performs any actions appropriate in response to the user having changed something. */
    @Override
    public void onValueChanged() {
        view.enableLoginButton(isFullFilled());
    }

    private boolean isFullFilled() {
        return view.getAccessKey() != null && !view.getAccessKey().isEmpty() && view.getSecretKey() != null &&
               !view.getSecretKey().isEmpty();
    }
}
