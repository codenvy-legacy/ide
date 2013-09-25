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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.marshaller.ConfigurationListUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.ConfigurationOptionInfoListUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.Configuration;
import com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption;
import com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter that allow user to edit application configuration.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class EditConfigurationPresenter implements EditConfigurationView.ActionDelegate {
    private EditConfigurationView              view;
    private BeanstalkClientService             service;
    private ContainerTabPainPresenter          containerTabPainPresenter;
    private EventBus                           eventBus;
    private ResourceProvider                   resourceProvider;
    private AWSLocalizationConstant            constant;
    private ServerTabPainPresenter             serverTabPainPresenter;
    private LoadBalancerTabPainPresenter       loadBalancerTabPainPresenter;
    private EnvironmentInfo                    environmentInfo;
    private NotificationManager                notificationManager;
    private JsonArray<ConfigurationOptionInfo> configurationOptionInfoList;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param containerTabPainPresenter
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param serverTabPainPresenter
     * @param loadBalancerTabPainPresenter
     * @param notificationManager
     */
    @Inject
    protected EditConfigurationPresenter(EditConfigurationView view, BeanstalkClientService service,
                                         ContainerTabPainPresenter containerTabPainPresenter, EventBus eventBus,
                                         ResourceProvider resourceProvider, AWSLocalizationConstant constant,
                                         ServerTabPainPresenter serverTabPainPresenter,
                                         LoadBalancerTabPainPresenter loadBalancerTabPainPresenter,
                                         NotificationManager notificationManager) {

        this.view = view;
        this.service = service;
        this.containerTabPainPresenter = containerTabPainPresenter;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.serverTabPainPresenter = serverTabPainPresenter;
        this.loadBalancerTabPainPresenter = loadBalancerTabPainPresenter;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);

        AcceptsOneWidget serverTab = view.addServerTabPain("Server");
        serverTabPainPresenter.go(serverTab);

        AcceptsOneWidget loadBalancerTab = view.addLoadBalancerTabPain("Load Balancer");
        loadBalancerTabPainPresenter.go(loadBalancerTab);

        AcceptsOneWidget containerTab = view.addContainerTabPain("Container");
        containerTabPainPresenter.go(containerTab);
    }

    /** Show main dialog window. */
    public void showDialog(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
        if (!view.isShown()) {
            view.focusInFirstTab();
            view.showDialog();
            getConfigurationOptions();
        }
    }

    /** Get configuration options for selected solution stack technology. */
    private void getConfigurationOptions() {
        DtoClientImpls.SolutionStackConfigurationOptionsRequestImpl solutionStackConfigurationOptionsRequest =
                DtoClientImpls.SolutionStackConfigurationOptionsRequestImpl.make();
        solutionStackConfigurationOptionsRequest.setSolutionStackName(environmentInfo.getSolutionStackName());

        ConfigurationOptionInfoListUnmarshaller unmarshaller = new ConfigurationOptionInfoListUnmarshaller();

        try {
            service.getSolutionStackConfigurationOptions(solutionStackConfigurationOptionsRequest,
                                                         new AsyncRequestCallback<JsonArray<ConfigurationOptionInfo>>(unmarshaller) {
                                                             @Override
                                                             protected void onSuccess(JsonArray<ConfigurationOptionInfo> result) {
                                                                 configurationOptionInfoList = result;
                                                                 getConfigurationsList();
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 getConfigurationsList();
                                                             }
                                                         });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** get configuration list for selected environment. */
    private void getConfigurationsList() {
        DtoClientImpls.ConfigurationRequestImpl configurationRequest = DtoClientImpls.ConfigurationRequestImpl.make();
        configurationRequest.setEnvironmentName(environmentInfo.getName());

        ConfigurationListUnmarshaller unmarshaller = new ConfigurationListUnmarshaller();

        try {
            service.getEnvironmentConfigurations(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                                 configurationRequest, new AsyncRequestCallback<JsonArray<Configuration>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<Configuration> result) {
                    if (result.size() > 0) {
                        showConfiguration(result.get(0));
                    } else {
                        String message = constant.getEnvironmentConfigurationFailed();
                        Notification notification = new Notification(message, ERROR);
                        notificationManager.showNotification(notification);
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = constant.getEnvironmentConfigurationFailed();
                    if (exception instanceof ServerException && exception.getMessage() != null) {
                        message += "<br>" + exception.getMessage();
                    }

                    Notification notification = new Notification(message, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Assign configuration between nested presenters.
     *
     * @param envConfiguration
     *         environment configuration.
     */
    private void showConfiguration(Configuration envConfiguration) {
        serverTabPainPresenter.setConfiguration(envConfiguration.getOptions(), configurationOptionInfoList);
        containerTabPainPresenter.setConfiguration(envConfiguration.getOptions(), null);
        loadBalancerTabPainPresenter.setConfiguration(envConfiguration.getOptions(), null);
    }

    /** {@inheritDoc} */
    @Override
    public void onApplyButtonCLicked() {
        JsonArray<ConfigurationOption> options = JsoArray.create();

        options.addAll(serverTabPainPresenter.getConfigurationOptions());
        options.addAll(containerTabPainPresenter.getConfigurationOptions());
        options.addAll(loadBalancerTabPainPresenter.getConfigurationOptions());

        DtoClientImpls.UpdateEnvironmentRequestImpl updateEnvironmentRequest = DtoClientImpls.UpdateEnvironmentRequestImpl.make();
        updateEnvironmentRequest.setOptions(options);

        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.updateEnvironment(environmentInfo.getId(), updateEnvironmentRequest,
                                      new AsyncRequestCallback<EnvironmentInfo>(unmarshaller) {
                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              view.close();
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              String message = constant.updateEnvironmentConfigurationFailed(environmentInfo.getName());
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              Notification notification = new Notification(message, ERROR);
                                              notificationManager.showNotification(notification);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }
}
