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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Configuration;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStackConfigurationOptionsRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateEnvironmentRequest;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for edit environment's configuration.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EditConfigurationPresenter.java Oct 5, 2012 5:59:23 PM azatsarynnyy $
 */
public class EditConfigurationPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
        VfsChangedHandler, EditConfigurationHandler, ViewClosedHandler {

    interface Display extends IsView {
        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        // Server tab
        HasValue<String> getEC2InstanceTypeField();

        void setEC2InstanceTypeValues(String[] values, String selectedValue);

        TextFieldItem getEC2SecurityGroupsField();

        TextFieldItem getKeyNameField();

        HasValue<String> getMonitoringIntervalField();

        void setMonitoringIntervalValues(String[] values, String selectedValue);

        TextFieldItem getImageIdField();

        // Load Balancer tab
        TextFieldItem getAppHealthCheckCheckUrlField();

        TextFieldItem getHealthCheckIntervalField();

        TextFieldItem getHealthCheckTimeoutField();

        TextFieldItem getHealthyThresholdField();

        TextFieldItem getUnhealthyThresholdField();

        // Container tab
        TextFieldItem getInitialJVMHeapSizeField();

        TextFieldItem getMaximumJVMHeapSizeField();

        TextFieldItem getMaxPermSizeField();

        TextFieldItem getJVMOptionsField();
    }

    private Display display;

    private ProjectModel openedProject;

    private VirtualFileSystemInfo vfsInfo;

    private EnvironmentInfo environmentInfo;

    /** List of information about configuration options. */
    private List<ConfigurationOptionInfo> configurationOptionInfoList;

    private Map<String, ConfigurationOption> modifiedOptionsMap;

    /** List of modified configuration options to save. */
    private List<ConfigurationOption> modifiedOptionsList;

    private UpdateEnvironmentStartedHandler updateEnvironmentStartedHandler;

    public EditConfigurationPresenter() {
        IDE.addHandler(EditConfigurationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                applyConfiguration();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getEC2InstanceTypeField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("InstanceType", event.getValue());
            }
        });

        display.getEC2SecurityGroupsField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("SecurityGroups", event.getValue());
            }
        });

        display.getKeyNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("EC2KeyName", event.getValue());
            }
        });

        display.getMonitoringIntervalField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("MonitoringInterval", event.getValue());
            }
        });

        display.getImageIdField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("ImageId", event.getValue());
            }
        });

        display.getAppHealthCheckCheckUrlField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("Application Healthcheck URL", event.getValue());
            }
        });

        display.getHealthCheckIntervalField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("Interval", event.getValue());
            }
        });

        display.getHealthCheckTimeoutField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("Timeout", event.getValue());
            }
        });

        display.getHealthyThresholdField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("HealthyThreshold", event.getValue());
            }
        });

        display.getUnhealthyThresholdField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("UnhealthyThreshold", event.getValue());
            }
        });

        display.getInitialJVMHeapSizeField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("Xms", event.getValue());
            }
        });

        display.getMaximumJVMHeapSizeField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("Xmx", event.getValue());
            }
        });

        display.getMaxPermSizeField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("XX:MaxPermSize", event.getValue());
            }
        });

        display.getJVMOptionsField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                addOptionToListForSave("JVM Options", event.getValue());
            }
        });

    }

    /**
     * Add modified configuration option to list for save.
     *
     * @param optionName
     *         the name of {@link ConfigurationOption} which is modified
     * @param optionValue
     *         the new value of {@link ConfigurationOption} which is modified
     */
    private void addOptionToListForSave(String optionName, String optionValue) {
        ConfigurationOption configurationOption = modifiedOptionsMap.get(optionName);
        configurationOption.setValue(optionValue);
        int optionIndex = modifiedOptionsList.indexOf(configurationOption);
        if (optionIndex == -1) {
            modifiedOptionsList.add(configurationOption);
        }
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationHandler#onEditConfiguration
     * (org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationEvent) */
    @Override
    public void onEditConfiguration(EditConfigurationEvent event) {
        this.updateEnvironmentStartedHandler = event.getUpdateEnvironmentStartedHandler();
        environmentInfo = event.getEnvironment();
        if (environmentInfo != null) {
            getConfigurationOptions();
        }
    }

    private void getConfigurationOptions() {
        SolutionStackConfigurationOptionsRequest request =
                AWSExtension.AUTO_BEAN_FACTORY.solutionStackConfigurationOptionsRequest().as();
        request.setSolutionStackName(environmentInfo.getSolutionStackName());
        try {
            BeanstalkClientService.getInstance().getSolutionStackConfigurationOptions(
                    request,
                    new AwsAsyncRequestCallback<List<ConfigurationOptionInfo>>(new ConfigurationOptionInfoListUnmarshaller(),
                                                                               new LoggedInHandler() {

                                                                                   @Override
                                                                                   public void onLoggedIn() {
                                                                                       getConfigurationOptions();
                                                                                   }
                                                                               }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            // If get the list of ConfigurationOptionInfo is fail then show the view anyway.
                            // In this case, drop-down lists will have only one value (current).
                            getConfigurationsList();
                        }

                        @Override
                        protected void onSuccess(List<ConfigurationOptionInfo> result) {
                            configurationOptionInfoList = result;
                            getConfigurationsList();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get the list of environment configurations. */
    private void getConfigurationsList() {
        ConfigurationRequest configurationRequest = AWSExtension.AUTO_BEAN_FACTORY.configurationRequest().as();
        configurationRequest.setEnvironmentName(environmentInfo.getName());
        try {
            BeanstalkClientService.getInstance().getEnvironmentConfigurations(vfsInfo.getId(), openedProject.getId(),
                                                                              configurationRequest,
                                                                              new AwsAsyncRequestCallback<List<Configuration>>(
                                                                                      new ConfigurationListUnmarshaller(),
                                                                                      new LoggedInHandler() {
                                                                                          @Override
                                                                                          public void onLoggedIn() {
                                                                                              getConfigurationsList();
                                                                                          }
                                                                                      }, null) {
                                                                                  @Override
                                                                                  protected void onSuccess(List<Configuration> result) {
                                                                                      // TODO make possible to edit several configuration
                                                                                      // templates
                                                                                      if (result.size() > 0) {
                                                                                          showConfiguration(result.get(0));
                                                                                      } else {
                                                                                          String message =
                                                                                                  AWSExtension.LOCALIZATION_CONSTANT
                                                                                                              .getEnvironmentConfigurationFailed();
                                                                                          Dialogs.getInstance().showError(message);
                                                                                      }
                                                                                  }

                                                                                  @Override
                                                                                  protected void processFail(Throwable exception) {
                                                                                      String message = AWSExtension.LOCALIZATION_CONSTANT
                                                                                                                   .getEnvironmentConfigurationFailed();
                                                                                      if (exception instanceof ServerException &&
                                                                                          ((ServerException)exception).getMessage() !=
                                                                                          null) {
                                                                                          message += "<br>" + ((ServerException)exception)
                                                                                                  .getMessage();
                                                                                      }
                                                                                      Dialogs.getInstance().showError(message);
                                                                                  }
                                                                              });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Show Environment Configuration view.
     *
     * @param envConfiguration
     *         {@link Configuration}
     */
    private void showConfiguration(Configuration envConfiguration) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
        }

        modifiedOptionsList = new ArrayList<ConfigurationOption>();
        modifiedOptionsMap = new HashMap<String, ConfigurationOption>();
        for (ConfigurationOption option : envConfiguration.getOptions()) {
            modifiedOptionsMap.put(option.getName(), option);

            if (option.getName().equals("InstanceType")) {
                String[] valueOptions = getValueOptionsForConfigurationOption(option);
                display.setEC2InstanceTypeValues(valueOptions, option.getValue());
            } else if (option.getName().equals("SecurityGroups")) {
                display.getEC2SecurityGroupsField().setValue(option.getValue(), false);
            } else if (option.getName().equals("EC2KeyName")) {
                display.getKeyNameField().setValue(option.getValue(), false);
            } else if (option.getName().equals("MonitoringInterval")) {
                String[] valueOptions = getValueOptionsForConfigurationOption(option);
                display.setMonitoringIntervalValues(valueOptions, option.getValue());
            } else if (option.getName().equals("ImageId")) {
                display.getImageIdField().setValue(option.getValue(), false);
            } else if (option.getName().equals("Application Healthcheck URL")) {
                display.getAppHealthCheckCheckUrlField().setValue(option.getValue(), false);
            } else if (option.getName().equals("Interval")) {
                display.getHealthCheckIntervalField().setValue(option.getValue(), false);
            } else if (option.getName().equals("Timeout")) {
                display.getHealthCheckTimeoutField().setValue(option.getValue(), false);
            } else if (option.getName().equals("HealthyThreshold")) {
                display.getHealthyThresholdField().setValue(option.getValue(), false);
            } else if (option.getName().equals("UnhealthyThreshold")) {
                display.getUnhealthyThresholdField().setValue(option.getValue(), false);
            } else if (option.getName().equals("Xms")) {
                display.getInitialJVMHeapSizeField().setValue(option.getValue(), false);
            } else if (option.getName().equals("Xmx")) {
                display.getMaximumJVMHeapSizeField().setValue(option.getValue(), false);
            } else if (option.getName().equals("XX:MaxPermSize")) {
                display.getMaxPermSizeField().setValue(option.getValue(), false);
            } else if (option.getName().equals("JVM Options")) {
                display.getJVMOptionsField().setValue(option.getValue(), false);
            }
        }

        // bind Display after settings field values to avoid invoking ValueChangeHandler of fields
        bindDisplay();
    }

    /**
     * Returns array of value options for the specified {@link ConfigurationOption}.
     *
     * @param option
     *         the {@link ConfigurationOption}
     * @return array of value options
     */
    private String[] getValueOptionsForConfigurationOption(ConfigurationOption option) {
        if (configurationOptionInfoList == null) {
            return new String[]{option.getValue()};
        }

        for (ConfigurationOptionInfo optionInfo : configurationOptionInfoList) {
            if (optionInfo.getName().equals(option.getName())) {
                List<String> valueOptionsList = optionInfo.getValueOptions();
                return valueOptionsList.toArray(new String[valueOptionsList.size()]);
            }
        }

        return new String[]{option.getValue()};
    }

    /** Save the modified configuration. */
    private void applyConfiguration() {
        UpdateEnvironmentRequest updateEnvironmentRequest =
                AWSExtension.AUTO_BEAN_FACTORY.updateEnvironmentRequest().as();
        updateEnvironmentRequest.setOptions(modifiedOptionsList);

        AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();

        try {
            BeanstalkClientService.getInstance().updateEnvironment(
                    environmentInfo.getId(),
                    updateEnvironmentRequest,
                    new AwsAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
                                                                 new LoggedInHandler() {

                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         applyConfiguration();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void onSuccess(EnvironmentInfo result) {
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            if (updateEnvironmentStartedHandler != null) {
                                updateEnvironmentStartedHandler.onUpdateEnvironmentStarted(environmentInfo);
                            }
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            String message =
                                    AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentConfigurationFailed(environmentInfo.getName());
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                            Dialogs.getInstance().showError(message);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        this.openedProject = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        this.openedProject = event.getProject();
    }

}
