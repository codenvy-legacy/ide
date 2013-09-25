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
