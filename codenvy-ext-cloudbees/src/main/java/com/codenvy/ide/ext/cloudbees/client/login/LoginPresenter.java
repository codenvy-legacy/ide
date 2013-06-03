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
package com.codenvy.ide.ext.cloudbees.client.login;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for logging on CloudBees.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 3:56:55 PM anya $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView                     view;
    private LoggedInHandler               loggedIn;
    private LoginCanceledHandler          loginCanceled;
    private CloudBeesClientService        service;
    private EventBus                      eventBus;
    private CloudBeesLocalizationConstant constant;
    private ConsolePart                   console;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param constant
     * @param console
     */
    @Inject
    protected LoginPresenter(LoginView view, CloudBeesClientService service, EventBus eventBus, CloudBeesLocalizationConstant constant,
                             ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.constant = constant;
        this.console = console;
    }

    /** Shows dialog. */
    public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;

        showDialog();
    }

    /** Shows dialog. */
    public void showDialog() {
        if (!view.isShown()) {
            view.setEnableLoginButton(false);
            view.focusInEmailField();
            view.setPassword("");
            view.setError("");

            view.showDialog();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onLoginClicked() {
        final String email = view.getEmail();
        final String password = view.getPassword();

        try {
            service.login(email, password, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print(constant.loginSuccess());
                    if (loggedIn != null) {
                        loggedIn.onLoggedIn();
                    }
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    view.setError(constant.loginFailed());
                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        if (loginCanceled != null) {
            loginCanceled.onLoginCanceled();
        }

        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        updateComponent();
    }

    /** Updates component on the view. */
    private void updateComponent() {
        view.setEnableLoginButton(isFieldsFullFilled());
    }

    /**
     * Check whether necessary fields are fullfilled.
     *
     * @return if <code>true</code> all necessary fields are fullfilled
     */
    private boolean isFieldsFullFilled() {
        return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getPassword() != null && !view.getPassword().isEmpty());
    }
}