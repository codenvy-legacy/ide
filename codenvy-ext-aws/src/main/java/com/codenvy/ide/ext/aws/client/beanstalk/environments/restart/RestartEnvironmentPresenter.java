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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.restart;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class RestartEnvironmentPresenter implements RestartEnvironmentView.ActionDelegate {
    private RestartEnvironmentView         view;
    private EventBus                       eventBus;
    private ConsolePart                    console;
    private BeanstalkClientService         service;
    private AWSLocalizationConstant        constant;
    private LoginPresenter                 loginPresenter;
    private EnvironmentInfo                environmentInfo;

    @Inject
    public RestartEnvironmentPresenter(RestartEnvironmentView view, EventBus eventBus, ConsolePart console,
                                       BeanstalkClientService service, AWSLocalizationConstant constant,
                                       LoginPresenter loginPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;

        this.view.setDelegate(this);
    }

    public void showDialog(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
        if (!environmentInfo.getStatus().equals(EnvironmentStatus.Ready)) {
            Window.alert("Environment is in an invalid state for this operation. Must be Ready");
            return;
        }

        if (!view.isShown()) {
            view.showDialog();
        }

        view.setRestartQuestion(constant.restartAppServerQuestion(environmentInfo.getName()));
    }

    @Override
    public void onRestartButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRestartButtonClicked();
            }
        };

        try {
            service.restartApplicationServer(environmentInfo.getId(),
                                             new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                                 @Override
                                                 protected void processFail(Throwable exception) {
                                                     String message = constant.rebuildEnvironmentFailed(environmentInfo.getId());
                                                     if (exception instanceof ServerException &&
                                                         exception.getMessage() != null) {
                                                         message += "<br>" + exception.getMessage();
                                                     }

                                                     console.print(message);
                                                 }

                                                 @Override
                                                 protected void onSuccess(Object result) {
                                                    view.close();
                                                 }
                                             });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }
}
