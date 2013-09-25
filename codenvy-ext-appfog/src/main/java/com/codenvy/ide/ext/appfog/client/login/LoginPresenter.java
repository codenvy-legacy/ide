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
package com.codenvy.ide.ext.appfog.client.login;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.appfog.client.AppFogExtension;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.marshaller.SystemInfoUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.SystemInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for logging on AppFog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView                  view;
    /** The last target, that user logged in. */
    private String                     target;
    private LoggedInHandler            loggedIn;
    private LoginCanceledHandler       loginCanceled;
    private AppfogClientService        service;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private NotificationManager        notificationManager;

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
    protected LoginPresenter(LoginView view, EventBus eventBus, AppfogClientService service, AppfogLocalizationConstant constant,
                             NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.service = service;
        this.constant = constant;
        this.target = AppFogExtension.DEFAULT_SERVER;
        this.notificationManager = notificationManager;
    }

    /** Shows dialog. */
    public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, String loginUrl) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        if (loginUrl != null) {
            target = loginUrl;
            if (!target.startsWith("http")) {
                target = "http://" + target;
            }
        }

        showDialog();
    }

    /** Shows dialog. */
    public void showDialog() {
        if (!view.isShown()) {
            view.setEnableLoginButton(false);
            view.setTarget(target);
            view.focusInEmailField();
            view.setPassword("");
            view.setError("");

            getSystemInformation();

            view.showDialog();
        }
    }

    /** Get AppFog system information to fill the login field, if user is logged in. */
    protected void getSystemInformation() {
        SystemInfoUnmarshaller unmarshaller = new SystemInfoUnmarshaller();

        try {
            service.getSystemInfo(AppFogExtension.DEFAULT_SERVER,
                                  new AppfogAsyncRequestCallback<SystemInfo>(unmarshaller, loggedIn, loginCanceled, eventBus, constant,
                                                                             this, notificationManager) {
                                      @Override
                                      protected void onSuccess(SystemInfo result) {
                                          view.setEmail(result.getUser());
                                      }
                                  });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), Notification.Type.ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onLoginClicked() {
        final String enteredServer = view.getTarget();
        final String email = view.getEmail();
        final String password = view.getPassword();

        try {
            service.login(enteredServer, email, password,
                          new AsyncRequestCallback<String>() {
                              @Override
                              protected void onSuccess(String result) {
                                  target = enteredServer;
                                  Notification notification = new Notification(constant.loginSuccess(), Notification.Type.INFO);
                                  notificationManager.showNotification(notification);

                                  if (loggedIn != null) {
                                      loggedIn.onLoggedIn();
                                  }
                                  view.close();
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  if (exception instanceof ServerException) {
                                      ServerException serverException = (ServerException)exception;
                                      if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                                          && serverException.getMessage() != null
                                          && serverException.getMessage().contains("Can't access target.")) {
                                          view.setError(constant.loginViewErrorUnknownTarget());
                                          return;
                                      } else if (HTTPStatus.OK != serverException.getHTTPStatus() &&
                                                 serverException.getMessage() != null
                                                 && serverException.getMessage()
                                                                   .contains("Operation not permitted")) {
                                          view.setError(constant.loginViewErrorInvalidUserOrPassword());
                                          return;
                                      } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                                                 && serverException.getMessage() != null
                                                 && serverException.getMessage().contains("Invalid password")) {
                                          view.setError(constant.loginViewErrorInvalidUserOrPassword());
                                          return;
                                      }
                                      // otherwise will be called method from superclass.
                                  }
                                  // TODO doesn't show invalid password error
                                  Notification notification = new Notification(exception.getMessage(), Notification.Type.ERROR);
                                  notificationManager.showNotification(notification);
                                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                              }
                          });
        } catch (RequestException e) {
            Window.alert(e.getMessage());
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