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
public class LoadBalancerTabPainPresenter implements Presenter, LoadBalancerTabPainView.ActionDelegate, HasConfigurationProperty {
    private LoadBalancerTabPainView view;

    @Inject
    public LoadBalancerTabPainPresenter(LoadBalancerTabPainView view) {
        this.view = view;
    }

    @Override
    public JsonArray<ConfigurationOption> getConfigurationOptions() {
        JsonArray<ConfigurationOption> options = JsonCollections.createArray();

        DtoClientImpls.ConfigurationOptionImpl healthCheckUrlOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        healthCheckUrlOpt.setName("Application Healthcheck URL");
        healthCheckUrlOpt.setValue(view.getHealthCheckUrl());

        DtoClientImpls.ConfigurationOptionImpl intervalOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        intervalOpt.setName("Interval");
        intervalOpt.setValue(view.getHealthCheckUrl());

        DtoClientImpls.ConfigurationOptionImpl timeoutOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        timeoutOpt.setName("Timeout");
        timeoutOpt.setValue(view.getHealthCheckUrl());

        DtoClientImpls.ConfigurationOptionImpl healthyThresholdOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        healthyThresholdOpt.setName("HealthyThreshold");
        healthyThresholdOpt.setValue(view.getHealthCheckUrl());

        DtoClientImpls.ConfigurationOptionImpl unHealthyThresholdOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        unHealthyThresholdOpt.setName("UnhealthyThreshold");
        unHealthyThresholdOpt.setValue(view.getHealthCheckUrl());

        options.add(healthCheckUrlOpt);
        options.add(intervalOpt);
        options.add(timeoutOpt);
        options.add(healthyThresholdOpt);
        options.add(unHealthyThresholdOpt);

        return options;
    }

    @Override
    public void setConfiguration(JsonArray<ConfigurationOption> configuration, JsonArray<ConfigurationOptionInfo> configurationOptionInfo) {
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

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
