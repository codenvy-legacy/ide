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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

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
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoggedInHandler               loggedIn;
    private LoginCanceledHandler          loginCanceled;
    private NotificationManager           notificationManager;
    private AsyncCallback<Boolean>        callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param service
     * @param constant
     * @param notificationManager
     */
    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, OpenShiftClientServiceImpl service, OpenShiftLocalizationConstant constant,
                             NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.service = service;
        this.constant = constant;
        this.notificationManager = notificationManager;

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
                    Notification notification = new Notification(constant.loginViewSuccessfullyLogined(), INFO);
                    notificationManager.showNotification(notification);
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
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    view.setError(constant.loginViewErrorInvalidUserOrPassword());

                    if (callback != null) {
                        callback.onSuccess(false);
                    }
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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
