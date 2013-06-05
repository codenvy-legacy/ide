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
package com.codenvy.ide.ext.openshift.client.login;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter to control user login form.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView                     view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoggedInHandler               loggedIn;
    private LoginCanceledHandler          loginCanceled;
    private AsyncCallback<Boolean>        callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param constant
     */
    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                             OpenShiftLocalizationConstant constant) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    /**
     * Show login form with callback support.
     *
     * @param callback
     *         callback which will be called after login success or fail.
     */
    public void showDialog(AsyncCallback<Boolean> callback) {
        this.callback = callback;

        showDialog();
    }

    /** Show login form. */
    public void showDialog() {
        if (!view.isShown()) {
            view.setEnableLoginButton(false);
            view.focusEmailField();
            view.setPassword("");
            view.setError("");

            view.showDialog();
        }
    }

    /**
     * Show login form with controlling handlers.
     *
     * @param loggedIn
     *         handler which will be executed when login is successful
     * @param loginCanceled
     *         handler which will be executed when login is fails
     */
    public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;

        showDialog();
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
                    console.print(constant.loginViewSuccessfullyLogined());
                    if (loggedIn != null) {
                        loggedIn.onLoggedIn();
                    }

                    if (callback != null) {
                        callback.onSuccess(true);
                    }
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    view.setError(constant.loginViewErrorInvalidUserOrPassword());

                    if (callback != null) {
                        callback.onSuccess(false);
                    }
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

    /** {@inheritDoc} */
    /** Updates component on the view. */
    private void updateComponent() {
        view.setEnableLoginButton(isFieldsFullFilled());
    }

    /**
     * Check whether necessary fields are full filled.
     *
     * @return if true all necessary fields are full filled
     */
    private boolean isFieldsFullFilled() {
        return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getPassword() != null && !view.getPassword().isEmpty());
    }
}
