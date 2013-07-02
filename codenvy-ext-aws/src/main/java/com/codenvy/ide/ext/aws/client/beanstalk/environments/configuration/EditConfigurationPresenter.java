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
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
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

//    private JsonStringMap<ConfigurationOption> modifiedOptionsMap;
//
//    /** List of modified configuration options to save. */
//    private JsonArray<ConfigurationOption> modifiedOptionsList;

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

    public void showDialog(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
        if (!view.isShown()) {
            view.showDialog();
            getConfigurationOptions();
        }
    }

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

    private void showConfiguration(Configuration envConfiguration) {
//        modifiedOptionsList = JsonCollections.createArray();
//        modifiedOptionsMap = JsonCollections.createStringMap();
//
//        for (int i = 0; i < envConfiguration.getOptions().size(); i++) {
//            ConfigurationOption option = envConfiguration.getOptions().get(i);
//            modifiedOptionsMap.put(option.getName(), option);
//        }

        serverTabPainPresenter.setConfiguration(envConfiguration.getOptions(), configurationOptionInfoList);
        containerTabPainPresenter.setConfiguration(envConfiguration.getOptions(), null);
        loadBalancerTabPainPresenter.setConfiguration(envConfiguration.getOptions(), null);
    }

    @Override
    public void onApplyButtonCLicked() {
        JsonArray<ConfigurationOption> options = JsonCollections.createArray();

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

    @Override
    public void onCancelButtonClicked() {

    }
}
