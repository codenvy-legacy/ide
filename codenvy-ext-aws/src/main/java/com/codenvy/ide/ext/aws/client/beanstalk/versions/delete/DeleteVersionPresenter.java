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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DeleteVersionPresenter implements DeleteVersionView.ActionDelegate {
    private DeleteVersionView                     view;
    private EventBus                              eventBus;
    private ConsolePart                           console;
    private BeanstalkClientService                service;
    private LoginPresenter                        loginPresenter;
    private AWSLocalizationConstant               constant;
    private ApplicationVersionInfo                version;
    private ResourceProvider                      resourceProvider;
    private AsyncCallback<ApplicationVersionInfo> callback;

    @Inject

    public DeleteVersionPresenter(DeleteVersionView view, EventBus eventBus, ConsolePart console,
                                  BeanstalkClientService service, LoginPresenter loginPresenter,
                                  AWSLocalizationConstant constant, ResourceProvider resourceProvider) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.constant = constant;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    public void showDialog(ApplicationVersionInfo version, AsyncCallback<ApplicationVersionInfo> callback) {
        this.version = version;
        this.callback = callback;

        if (!view.isShown()) {
            view.showDialog();
        }

        view.setDeleteQuestion(constant.deleteVersionQuestion(version.getVersionLabel()));
    }

    @Override
    public void onDeleteButtonCLicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteButtonCLicked();
            }
        };

        try {
            service.deleteVersion(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), version.getApplicationName(),
                                  version.getVersionLabel(), view.getDeleteS3Bundle(),
                                  new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                      @Override
                                      protected void processFail(Throwable exception) {
                                          String message = constant.deleteVersionFailed(version.getVersionLabel());
                                          if (exception instanceof ServerException && exception.getMessage() != null) {
                                              message += "<br>" + exception.getMessage();
                                          }

                                          console.print(message);

                                          if (callback != null) {
                                              callback.onSuccess(null);
                                          }
                                      }

                                      @Override
                                      protected void onSuccess(Object result) {
                                          view.close();

                                          if (callback != null) {
                                              callback.onSuccess(version);
                                          }
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
