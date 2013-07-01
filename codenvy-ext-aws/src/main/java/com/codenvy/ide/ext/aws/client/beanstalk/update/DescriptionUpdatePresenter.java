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
package com.codenvy.ide.ext.aws.client.beanstalk.update;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
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
public class DescriptionUpdatePresenter implements DescriptionUpdateView.ActionDelegate {
    private DescriptionUpdateView          view;
    private ConsolePart                    console;
    private LoginPresenter                 loginPresenter;
    private EventBus                       eventBus;
    private AWSLocalizationConstant        constant;
    private BeanstalkClientService         service;
    private ResourceProvider               resourceProvider;
    private AsyncCallback<ApplicationInfo> callback;
    private ApplicationInfo                applicationInfo;

    @Inject
    public DescriptionUpdatePresenter(DescriptionUpdateView view, ConsolePart console,
                                      LoginPresenter loginPresenter, EventBus eventBus,
                                      AWSLocalizationConstant constant, BeanstalkClientService service, ResourceProvider resourceProvider) {
        this.view = view;
        this.console = console;
        this.loginPresenter = loginPresenter;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    public void showDialog(ApplicationInfo applicationInfo, AsyncCallback<ApplicationInfo> callback) {
        this.applicationInfo = applicationInfo;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableUpdateButton(false);
            view.showDialog();
        }
    }

    @Override
    public void onUpdateClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onUpdateClicked();
            }
        };

        DtoClientImpls.UpdateApplicationRequestImpl updateApplicationRequest = DtoClientImpls.UpdateApplicationRequestImpl.make();
        updateApplicationRequest.setApplicationName(applicationInfo.getName());
        updateApplicationRequest.setDescription(view.getDescriptionValue());

        DtoClientImpls.ApplicationInfoImpl dtoApplicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(dtoApplicationInfo);

        try {
            service.updateApplication(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), updateApplicationRequest,
                                      new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              if (callback != null) {
                                                  callback.onSuccess(null);
                                              }
                                              String message = constant.updateApplicationFailed(applicationInfo.getName());
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              view.close();

                                              if (callback != null) {
                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onDescriptionFieldChangedValue() {
        view.enableUpdateButton(view.getDescriptionValue() != null && !view.getDescriptionValue().isEmpty());
    }
}
