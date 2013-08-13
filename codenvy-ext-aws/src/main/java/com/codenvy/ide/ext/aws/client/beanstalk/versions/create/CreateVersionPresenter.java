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
 * Presenter that allow user to create application.
 *
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

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param constant
     * @param loginPresenter
     * @param resourceProvider
     */
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

    /** Show main dialog window. */
    public void showDialog(String appName, AsyncCallback<ApplicationVersionInfo> callback) {
        this.appName = appName;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableCreateButton(false);
            view.showDialog();
            view.focusInVersionLabelField();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateButtonClicked() {
        warUrl = null;
        beforeCreation();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onVersionLabelKeyUp() {
        view.enableCreateButton(view.getVersionLabel() != null && view.getVersionLabel().length() > 0);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createVersion();
        }
    }

    /** Run application build before creating. */
    private void beforeCreation() {
        Project project = resourceProvider.getActiveProject();

        if (project != null) {
            projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
            eventBus.fireEvent(new BuildProjectEvent(project));
        } else {
            Window.alert("You must open AWS project to deploy it.");
        }
    }

    /** Create new version for application. */
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
