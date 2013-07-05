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
 * Presenter that show user environment configuration.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoadBalancerTabPainPresenter implements Presenter, LoadBalancerTabPainView.ActionDelegate, HasConfigurationProperty {
    private LoadBalancerTabPainView        view;
    private JsonArray<ConfigurationOption> configuration;

    /**
     * Create view.
     *
     * @param view
     */
    @Inject
    public LoadBalancerTabPainPresenter(LoadBalancerTabPainView view) {
        this.view = view;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<ConfigurationOption> getConfigurationOptions() {
        JsonArray<ConfigurationOption> options = JsonCollections.createArray();

        for (int i = 0; i < configuration.size(); i++) {
            ConfigurationOption option = configuration.get(i);

            if ("Application Healthcheck URL".equals(option.getName()) && view.isHealthCheckUrlModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("Application Healthcheck URL");
                dtoOption.setValue(view.getHealthCheckUrl());
                options.add(dtoOption);
            }

            if ("Interval".equals(option.getName()) && view.isHealthCheckIntervalModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("Interval");
                dtoOption.setValue(view.getHealthCheckInterval());
                options.add(dtoOption);
            }

            if ("Timeout".equals(option.getName()) && view.isHealthCheckTimeOutModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("Timeout");
                dtoOption.setValue(view.getHealthCheckTimeOut());
                options.add(dtoOption);
            }

            if ("HealthyThreshold".equals(option.getName()) && view.isHealthCheckCountThresholdModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("HealthyThreshold");
                dtoOption.setValue(view.getHealthCheckCountThreshold());
                options.add(dtoOption);
            }

            if ("UnhealthyThreshold".equals(option.getName()) && view.isUnhealthyCheckCountThresholdModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("UnhealthyThreshold");
                dtoOption.setValue(view.getUnhealthyCheckCountThreshold());
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
            if (option.getName().equals("Application Healthcheck URL")) {
                view.setHealthCheckUrl(option.getValue());
            } else if (option.getName().equals("Interval")) {
                view.setHealthCheckInterval(option.getValue());
            } else if (option.getName().equals("Timeout")) {
                view.setHealthCheckTimeOut(option.getValue());
            } else if (option.getName().equals("HealthyThreshold")) {
                view.setHealthCheckCountThreshold(option.getValue());
            } else if (option.getName().equals("UnhealthyThreshold")) {
                view.setUnhealthyCheckCountThreshold(option.getValue());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
