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
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.update.DescriptionUpdatePresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.create.CreateVersionPresenter;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;

/**
 * Presenter to allow user view application info and control description.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class MainTabPainPresenter implements Presenter, MainTabPainView.ActionDelegate {
    private MainTabPainView            view;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private AWSLocalizationConstant    constant;
    private LoginPresenter             loginPresenter;
    private DescriptionUpdatePresenter descriptionUpdatePresenter;
    private ApplicationInfo            applicationInfo;
    private BeanstalkClientService     service;
    private ResourceProvider           resourceProvider;
    private CreateVersionPresenter     createNewVersionPresenter;
    private LaunchEnvironmentPresenter launchEnvironmentPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param descriptionUpdatePresenter
     * @param service
     * @param resourceProvider
     * @param launchEnvironmentPresenter
     * @param createNewVersionPresenter
     */
    @Inject
    public MainTabPainPresenter(MainTabPainView view, EventBus eventBus, ConsolePart console,
                                AWSLocalizationConstant constant, LoginPresenter loginPresenter,
                                DescriptionUpdatePresenter descriptionUpdatePresenter, BeanstalkClientService service,
                                ResourceProvider resourceProvider, LaunchEnvironmentPresenter launchEnvironmentPresenter,
                                CreateVersionPresenter createNewVersionPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.descriptionUpdatePresenter = descriptionUpdatePresenter;
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.launchEnvironmentPresenter = launchEnvironmentPresenter;
        this.createNewVersionPresenter = createNewVersionPresenter;

        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** Get application information. */
    public void loadApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                loadApplication();
            }
        };

        DtoClientImpls.ApplicationInfoImpl dtoApplicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(dtoApplicationInfo);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                       new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                           @Override
                                           protected void processFail(Throwable exception) {
                                               eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                               console.print(exception.getMessage());
                                           }

                                           @Override
                                           protected void onSuccess(ApplicationInfo result) {
                                               applicationInfo = result;

                                               String createdTime =
                                                       DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                                                     .format(new Date((long)result.getCreated()));

                                               String updatedTime =
                                                       DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                                                     .format(new Date((long)result.getUpdated()));

                                               view.setApplicationName(result.getName());
                                               view.setDescription(result.getDescription());
                                               view.setCreationDate(createdTime);
                                               view.setUpdateDate(updatedTime);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEditDescriptionButtonClicked() {
        descriptionUpdatePresenter.showDialog(applicationInfo, new AsyncCallback<ApplicationInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(ApplicationInfo result) {
                applicationInfo = result;

                String createdTime =
                        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                      .format(new Date((long)result.getCreated()));

                String updatedTime =
                        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                      .format(new Date((long)result.getUpdated()));

                view.setApplicationName(result.getName());
                view.setDescription(result.getDescription());
                view.setCreationDate(createdTime);
                view.setUpdateDate(updatedTime);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteApplicationButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteApplicationButtonClicked();
            }
        };

        try {
            service.deleteApplication(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                      new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                              console.print(exception.getMessage());
                                          }

                                          @Override
                                          protected void onSuccess(Object result) {

                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateNewVersionButtonClicked() {
        createNewVersionPresenter.showDialog(applicationInfo.getName(), new AsyncCallback<ApplicationVersionInfo>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(ApplicationVersionInfo result) {
                String updatedTime =
                        DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                      .format(new Date((long)result.getUpdated()));

                view.setUpdateDate(updatedTime);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onLaunchNewEnvironmentButtonClicked() {
        launchEnvironmentPresenter
                .showDialog(applicationInfo.getVersions().get(0), applicationInfo.getName(), new AsyncCallback<EnvironmentInfo>() {
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
