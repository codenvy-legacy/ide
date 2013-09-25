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

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOption;
import com.codenvy.ide.ext.aws.shared.beanstalk.ConfigurationOptionInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter that show user environment configuration.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ServerTabPainPresenter implements Presenter, ServerTabPainView.ActionDelegate, HasConfigurationProperty {
    private ServerTabPainView              view;
    private JsonArray<ConfigurationOption> configuration;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ServerTabPainPresenter(ServerTabPainView view) {
        this.view = view;

        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<ConfigurationOption> getConfigurationOptions() {
        JsonArray<ConfigurationOption> options = JsonCollections.createArray();

        for (int i = 0; i < configuration.size(); i++) {
            ConfigurationOption option = configuration.get(i);

            if ("InstanceType".equals(option.getName()) && view.isEc2InstanceTypeModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("InstanceType");
                dtoOption.setValue(view.getEc2InstanceType());
                options.add(dtoOption);
            }

            if ("SecurityGroups".equals(option.getName()) && view.isEc2SecurityGroupModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("SecurityGroups");
                dtoOption.setValue(view.getEc2SecurityGroup());
                options.add(dtoOption);
            }

            if ("EC2KeyName".equals(option.getName()) && view.isHeyPairModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("EC2KeyName");
                dtoOption.setValue(view.getKeyPair());
                options.add(dtoOption);
            }

            if ("MonitoringInterval".equals(option.getName()) && view.isMonitoringIntervalModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("MonitoringInterval");
                dtoOption.setValue(view.getMonitoringInterval());
                options.add(dtoOption);
            }

            if ("ImageId".equals(option.getName()) && view.isAmiIdModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("ImageId");
                dtoOption.setValue(view.getAmiId());
                options.add(dtoOption);
            }
        }

        return options;
    }

    /** {@inheritDoc} */
    @Override
    public void setConfiguration(JsonArray<ConfigurationOption> configuration, JsonArray<ConfigurationOptionInfo> configurationOptionInfo) {
        view.resetModifiedFields();
        this.configuration = configuration;
        for (int i = 0; i < configuration.size(); i++) {
            ConfigurationOption option = configuration.get(i);

            if (option.getName().equals("InstanceType")) {
                JsonArray<String> valueOptions = getValueOptionsForConfigurationOption(option, configurationOptionInfo);
                view.setEc2InstanceTypes(valueOptions, option.getValue());
            } else if (option.getName().equals("SecurityGroups")) {
                view.setEc2SecurityGroup(option.getValue());
            } else if (option.getName().equals("EC2KeyName")) {
                view.setKeyPair(option.getValue());
            } else if (option.getName().equals("MonitoringInterval")) {
                JsonArray<String> valueOptions = getValueOptionsForConfigurationOption(option, configurationOptionInfo);
                view.setMonitoringInterval(valueOptions, option.getValue());
            } else if (option.getName().equals("ImageId")) {
                view.setAmiId(option.getValue());
            }
        }
    }

    /**
     * Get values for complex option, such as instance type and monitoring interval.
     *
     * @param option
     *         existed environment configuration.
     * @param configurationOptionInfo
     *         solution stack configuration.
     * @return array of possible values to configure.
     */
    private JsonArray<String> getValueOptionsForConfigurationOption(ConfigurationOption option,
                                                                    JsonArray<ConfigurationOptionInfo> configurationOptionInfo) {
        if (configurationOptionInfo == null) {
            return JsonCollections.createArray();
        }

        for (int i = 0; i < configurationOptionInfo.size(); i++) {
            ConfigurationOptionInfo optionInfo = configurationOptionInfo.get(i);
            if (optionInfo.getName().equals(option.getName())) {
                return optionInfo.getValueOptions();
            }
        }

        return JsonCollections.createArray();
    }
}
