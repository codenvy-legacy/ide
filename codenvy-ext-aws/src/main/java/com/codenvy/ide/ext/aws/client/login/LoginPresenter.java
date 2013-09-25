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
package com.codenvy.ide.ext.aws.client.login;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter to allow user login with credentials.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView               view;
    private LoggedInHandler         loggedInHandler;
    private LoginCanceledHandler    loginCanceledHandler;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private BeanstalkClientService  service;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param constant
     * @param service
     * @param notificationManager
     */
    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, AWSLocalizationConstant constant, BeanstalkClientService service,
                             NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog() {
        showDialog(null, null);
    }

    /** Show main dialog window. */
    public void showDialog(LoggedInHandler loggedInHandler, LoginCanceledHandler loginCanceledHandler) {
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;

        if (!view.isShown()) {
            view.setLoginResult("");
            view.showDialog();
            view.focusAccessKeyField();
        }
    }

    /** {@inheritDoc} */
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
                    Notification notification = new Notification(msg, INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    view.setLoginResult(constant.loginErrorInvalidKeyValue());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
        if (loginCanceledHandler != null) {
            loginCanceledHandler.onLoginCanceled();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        view.enableLoginButton(isFullFilled());
    }

    /**
     * Check if all user input fields are filled.
     *
     * @return true if all fields are filled.
     */
    private boolean isFullFilled() {
        return view.getAccessKey() != null && !view.getAccessKey().isEmpty() && view.getSecretKey() != null &&
               !view.getSecretKey().isEmpty();
    }
}
