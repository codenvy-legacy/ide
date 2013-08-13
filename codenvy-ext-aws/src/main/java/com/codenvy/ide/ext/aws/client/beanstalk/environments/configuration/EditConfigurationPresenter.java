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

import com.codenvy.ide.api.parts.ConsolePart;
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
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

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
    private ConsolePart                        console;
    private ResourceProvider                   resourceProvider;
    private AWSLocalizationConstant            constant;
    private ServerTabPainPresenter             serverTabPainPresenter;
    private LoadBalancerTabPainPresenter       loadBalancerTabPainPresenter;
    private EnvironmentInfo                    environmentInfo;
    private JsonArray<ConfigurationOptionInfo> configurationOptionInfoList;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param containerTabPainPresenter
     * @param eventBus
     * @param console
     * @param resourceProvider
     * @param constant
     * @param serverTabPainPresenter
     * @param loadBalancerTabPainPresenter
     */
    @Inject
    protected EditConfigurationPresenter(EditConfigurationView view, BeanstalkClientService service,
                                         ContainerTabPainPresenter containerTabPainPresenter, EventBus eventBus, ConsolePart console,
                                         ResourceProvider resourceProvider, AWSLocalizationConstant constant,
                                         ServerTabPainPresenter serverTabPainPresenter,
                                         LoadBalancerTabPainPresenter loadBalancerTabPainPresenter) {

        this.view = view;
        this.service = service;
        this.containerTabPainPresenter = containerTabPainPresenter;
        this.eventBus = eventBus;
        this.console = console;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.serverTabPainPresenter = serverTabPainPresenter;
        this.loadBalancerTabPainPresenter = loadBalancerTabPainPresenter;

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

        JsonArray<ConfigurationOptionInfo> configurationJsonArray = JsonCollections.createArray();
        ConfigurationOptionInfoListUnmarshaller unmarshaller = new ConfigurationOptionInfoListUnmarshaller(configurationJsonArray);

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
            console.print(e.getMessage());
        }
    }

    /** get configuration list for selected environment. */
    private void getConfigurationsList() {
        DtoClientImpls.ConfigurationRequestImpl configurationRequest = DtoClientImpls.ConfigurationRequestImpl.make();
        configurationRequest.setEnvironmentName(environmentInfo.getName());

        JsonArray<Configuration> configurationJsonArray = JsonCollections.createArray();
        ConfigurationListUnmarshaller unmarshaller = new ConfigurationListUnmarshaller(configurationJsonArray);

        try {
            service.getEnvironmentConfigurations(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                                 configurationRequest, new AsyncRequestCallback<JsonArray<Configuration>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<Configuration> result) {
                    if (result.size() > 0) {
                        showConfiguration(result.get(0));
                    } else {
                        String message = constant.getEnvironmentConfigurationFailed();
                        console.print(message);
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = constant.getEnvironmentConfigurationFailed();
                    if (exception instanceof ServerException && exception.getMessage() != null) {
                        message += "<br>" + exception.getMessage();
                    }

                    console.print(message);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

        DtoClientImpls.EnvironmentInfoImpl dtoEnvironmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(dtoEnvironmentInfo);

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

                                              console.print(message);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }
}
