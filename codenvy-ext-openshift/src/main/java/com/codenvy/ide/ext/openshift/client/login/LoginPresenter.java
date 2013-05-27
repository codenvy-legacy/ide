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
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftAutoBeanFactory;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class LoginPresenter implements LoginView.ActionDelegate {
    private LoginView                     view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftAutoBeanFactory      autoBeanFactory;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoggedInHandler               loggedIn;
    private LoginCanceledHandler          loginCanceled;


    @Inject
    protected LoginPresenter(LoginView view, EventBus eventBus, ConsolePart console, OpenShiftAutoBeanFactory autoBeanFactory,
                             OpenShiftClientServiceImpl service, OpenShiftLocalizationConstant constant) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.autoBeanFactory = autoBeanFactory;
        this.service = service;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    public void showDialog() {
        showDialog(null, null);
    }

    public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;

        if (!view.isShown()) {
            view.setEnableLoginButton(false);
            view.focusEmailField();
            view.setPassword("");
            view.setError("");

            getSystemInformation();

            view.showDialog();
        }
    }

    private void getSystemInformation() {
        try {
            AutoBean<RHUserInfo> userInfo = autoBeanFactory.rhUserInfo();
            AutoBeanUnmarshaller<RHUserInfo> unmarshaller = new AutoBeanUnmarshaller<RHUserInfo>(userInfo);
            service.getUserInfo(false, new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, loggedIn, loginCanceled, eventBus,
                                                                                     console, constant, this) {
                @Override
                protected void onSuccess(RHUserInfo result) {
                    view.setEmail(result.getRhlogin());
                }
            });

        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onLoginClicked() {
        final String email = view.getEmail();
        final String password = view.getPassword();

        try {
            service.login(email, password, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print("Login success");
                    if (loggedIn != null) {
                        loggedIn.onLoggedIn();
                    }
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onCancelClicked() {
        if (loginCanceled != null) {
            loginCanceled.onLoginCanceled();
        }

        view.close();
    }

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
