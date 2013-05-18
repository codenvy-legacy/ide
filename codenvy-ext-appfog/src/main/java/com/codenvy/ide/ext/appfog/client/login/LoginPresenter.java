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
package com.codenvy.ide.ext.appfog.client.login;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.appfog.client.*;
import com.codenvy.ide.ext.appfog.shared.SystemInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.rest.HTTPStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
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
    private AppfogAutoBeanFactory      autoBeanFactory;
    private AppfogClientService        service;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private ConsolePart                console;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param autoBeanFactory
     * @param service
     * @param constant
     */
    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, ConsolePart console, AppfogAutoBeanFactory autoBeanFactory,
                             AppfogClientService service, AppfogLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.autoBeanFactory = autoBeanFactory;
        this.service = service;
        this.constant = constant;
        this.console = console;
        this.target = AppFogExtension.DEFAULT_SERVER;
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
        try {
            AutoBean<SystemInfo> systemInfo = autoBeanFactory.systemInfo();
            AutoBeanUnmarshaller<SystemInfo> unmarshaller = new AutoBeanUnmarshaller<SystemInfo>(systemInfo);
            service.getSystemInfo(AppFogExtension.DEFAULT_SERVER,
                                  new AppfogAsyncRequestCallback<SystemInfo>(unmarshaller, loggedIn, loginCanceled, eventBus, constant,
                                                                             console, this) {
                                      @Override
                                      protected void onSuccess(SystemInfo result) {
                                          view.setEmail(result.getUser());
                                      }
                                  });
        } catch (RequestException e) {
            console.print(e.getMessage());
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
                                  console.print(constant.loginSuccess());
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
                                  console.print(exception.getMessage());
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