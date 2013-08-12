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
