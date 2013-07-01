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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.create;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationVersionInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateVersionPresenter implements CreateVersionView.ActionDelegate, ProjectBuiltHandler {
    private CreateVersionView                     view;
    private EventBus                              eventBus;
    private ConsolePart                           console;
    private BeanstalkClientService                service;
    private AWSLocalizationConstant               constant;
    private LoginPresenter                        loginPresenter;
    private ResourceProvider                      resourceProvider;
    private HandlerRegistration                   projectBuildHandler;
    private String                                warUrl;
    private String                                appName;
    private AsyncCallback<ApplicationVersionInfo> callback;

    @Inject

    public CreateVersionPresenter(CreateVersionView view, EventBus eventBus, ConsolePart console,
                                  BeanstalkClientService service, AWSLocalizationConstant constant,
                                  LoginPresenter loginPresenter, ResourceProvider resourceProvider) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    public void showDialog(String appName, AsyncCallback<ApplicationVersionInfo> callback) {
        this.appName = appName;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableCreateButton(false);
            view.showDialog();
            view.focusInVersionLabelField();
        }
    }

    @Override
    public void onCreateButtonClicked() {
        warUrl = null;
        beforeCreation();
    }

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    @Override
    public void onVersionLabelKeyUp() {
        view.enableCreateButton(view.getVersionLabel() != null && view.getVersionLabel().length() > 0);
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createVersion();
        }
    }

    private void beforeCreation() {
        Project project = resourceProvider.getActiveProject();

        if (project != null) {
            projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
            eventBus.fireEvent(new BuildProjectEvent(project));
        } else {
            Window.alert("You must open AWS project to deploy it.");
        }
    }

    private void createVersion() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createVersion();
            }
        };

        final String versionLabel = view.getVersionLabel();

        DtoClientImpls.CreateApplicationVersionRequestImpl createApplicationVersionRequest =
                DtoClientImpls.CreateApplicationVersionRequestImpl.make();
        createApplicationVersionRequest.setApplicationName(appName);
        createApplicationVersionRequest.setDescription(view.getDescription());
        createApplicationVersionRequest.setVersionLabel(versionLabel);
        createApplicationVersionRequest.setS3Bucket(view.getS3Bucket());
        createApplicationVersionRequest.setS3Key(view.getS3Key());
        createApplicationVersionRequest.setWar(warUrl);

        DtoClientImpls.ApplicationVersionInfoImpl applicationVersionInfo = DtoClientImpls.ApplicationVersionInfoImpl.make();
        ApplicationVersionInfoUnmarshaller unmarshaller = new ApplicationVersionInfoUnmarshaller(applicationVersionInfo);

        try {
            service.createVersion(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), createApplicationVersionRequest,
                                  new AwsAsyncRequestCallback<ApplicationVersionInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                      @Override
                                      protected void processFail(Throwable exception) {
                                          String message = constant.createVersionFailed(versionLabel);
                                          if (exception instanceof ServerException && exception.getMessage() != null) {
                                              message += "<br>" + exception.getMessage();
                                          }

                                          console.print(message);

                                          if (callback != null) {
                                              callback.onSuccess(null);
                                          }
                                      }

                                      @Override
                                      protected void onSuccess(ApplicationVersionInfo result) {
                                          view.close();

                                          if (callback != null) {
                                              callback.onSuccess(result);
                                          }
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
