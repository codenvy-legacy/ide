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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentsInfoListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.RequestStatusHandler;
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
public class DeployVersionPresenter implements DeployVersionView.ActionDelegate {
    private DeployVersionView              view;
    private EventBus                       eventBus;
    private ConsolePart                    console;
    private LoginPresenter                 loginPresenter;
    private BeanstalkClientService         service;
    private AWSLocalizationConstant        constant;
    private String                         appName;
    private String                         versionLabel;
    private AsyncCallback<EnvironmentInfo> callback;
    private ResourceProvider               resourceProvider;
    private JsonArray<EnvironmentInfo>     environments;
    private LaunchEnvironmentPresenter     launchEnvironmentPresenter;

    @Inject

    public DeployVersionPresenter(DeployVersionView view, EventBus eventBus, ConsolePart console,
                                  LoginPresenter loginPresenter, BeanstalkClientService service,
                                  AWSLocalizationConstant constant, ResourceProvider resourceProvider,
                                  LaunchEnvironmentPresenter launchEnvironmentPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.launchEnvironmentPresenter = launchEnvironmentPresenter;

        this.view.setDelegate(this);
    }

    public void showDialog(String appName, String versionLabel, AsyncCallback<EnvironmentInfo> callback) {
        this.appName = appName;
        this.versionLabel = versionLabel;
        this.callback = callback;

        if (!view.isShown()) {
            view.showDialog();
            getEnvironments();
        }
    }

    @Override
    public void onDeployButtonClicked() {
        if (view.getExistingEnvironmentMode()) {
            deployVersion();
        } else if (view.getNewEnvironmentMode()) {
            launchEnvironmentPresenter.showDialog(versionLabel, appName, new AsyncCallback<EnvironmentInfo>() {
                @Override
                public void onFailure(Throwable caught) {
                    //ignore
                }

                @Override
                public void onSuccess(EnvironmentInfo result) {
                    if (result == null) {
                        return;
                    }

                    console.print(constant.launchEnvironmentLaunching(result.getName()));

                    RequestStatusHandler environmentStatusHandler =
                            new EnvironmentRequestStatusHandler(constant.launchEnvironmentLaunching(result.getName()),
                                                                constant.launchEnvironmentSuccess(result.getName()), eventBus);

                    new EnvironmentStatusChecker(resourceProvider, resourceProvider.getActiveProject(), result, true,
                                                 environmentStatusHandler, eventBus, console, service, loginPresenter, constant)
                            .startChecking();
                }
            });
        }
    }

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    @Override
    public void onNewEnvironmentModeClicked() {
        view.enableEnvironmentsField(false);
        view.enableDeployButton(true);
    }

    @Override
    public void onExistingEnvironmentModeClicked() {
        view.enableEnvironmentsField(true);
        view.enableDeployButton(view.getEnvironmentsField() != null && view.getEnvironmentsField().length() > 0);
    }

    private void getEnvironments() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getEnvironments();
            }
        };
        EnvironmentsInfoListUnmarshaller unmarshaller = new EnvironmentsInfoListUnmarshaller();

        try {
            service.getEnvironments(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                    new AwsAsyncRequestCallback<JsonArray<EnvironmentInfo>>(unmarshaller, loggedInHandler, null,
                                                                                            loginPresenter) {
                                        @Override
                                        protected void processFail(Throwable exception) {
                                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                            console.print(exception.getMessage());
                                        }

                                        @Override
                                        protected void onSuccess(JsonArray<EnvironmentInfo> result) {
                                            JsonArray<String> environmentsName = JsonCollections.createArray();
                                            for (int i = 0; i < result.size(); i++) {
                                                EnvironmentInfo environment = result.get(i);
                                                if (environment.getStatus() == EnvironmentStatus.Ready) {
                                                    environmentsName.add(environment.getName());
                                                    environments.add(environment);
                                                }
                                            }

                                            view.setEnvironmentsValues(environmentsName);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    private void deployVersion() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                deployVersion();
            }
        };

        final String environmentId = view.getEnvironmentsField();

        DtoClientImpls.UpdateEnvironmentRequestImpl updateEnvironmentRequest = DtoClientImpls.UpdateEnvironmentRequestImpl.make();
        updateEnvironmentRequest.setVersionLabel(versionLabel);

        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.updateEnvironment(environmentId, updateEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              String message = constant.deployVersionFailed(versionLabel);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              view.close();
                                              if (callback != null) {
                                                  for (int i = 0; i < environments.size(); i++) {
                                                      if (environments.get(i).getId().equals(environmentId)) {
                                                          callback.onSuccess(environments.get(i));
                                                          return;
                                                      }
                                                  }
                                                  callback.onSuccess(null);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
