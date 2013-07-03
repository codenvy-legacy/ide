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
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ServerTabPainPresenter implements Presenter, ServerTabPainView.ActionDelegate, HasConfigurationProperty {
    private ServerTabPainView view;
    private JsonArray<ConfigurationOption> configuration;

    @Inject
    public ServerTabPainPresenter(ServerTabPainView view) {
        this.view = view;

        this.view.setDelegate(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

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
