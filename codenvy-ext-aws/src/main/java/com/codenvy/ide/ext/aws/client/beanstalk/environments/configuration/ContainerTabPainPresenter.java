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
public class ContainerTabPainPresenter implements Presenter, ContainerTabPainView.ActionDelegate, HasConfigurationProperty {
    private ContainerTabPainView view;

    @Inject
    public ContainerTabPainPresenter(ContainerTabPainView view) {
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

        DtoClientImpls.ConfigurationOptionImpl initialHeapOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        initialHeapOpt.setName("Xms");
        initialHeapOpt.setValue(view.getInitialHeapSize());

        DtoClientImpls.ConfigurationOptionImpl maxHeapSizeOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        maxHeapSizeOpt.setName("Xmx");
        maxHeapSizeOpt.setValue(view.getMaxHeapSize());

        DtoClientImpls.ConfigurationOptionImpl maxPermGenOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        maxPermGenOpt.setName("XX:MaxPermSize");
        maxPermGenOpt.setValue(view.getMaxPermGenSize());

        DtoClientImpls.ConfigurationOptionImpl jvmCommandLineOpt = DtoClientImpls.ConfigurationOptionImpl.make();
        jvmCommandLineOpt.setName("JVM Options");
        jvmCommandLineOpt.setValue(view.getJVMCommandLineOpt());

        options.add(initialHeapOpt);
        options.add(maxHeapSizeOpt);
        options.add(maxPermGenOpt);
        options.add(jvmCommandLineOpt);

        return options;
    }

    @Override
    public void setConfiguration(JsonArray<ConfigurationOption> configuration, JsonArray<ConfigurationOptionInfo> configurationOptionInfo) {
        for (int i = 0; i < configuration.size(); i++) {
            ConfigurationOption option = configuration.get(i);

            if (option.getName().equals("Xms")) {
                view.setInitialHeapSize(option.getValue());
            } else if (option.getName().equals("Xmx")) {
                view.setMaxHeapSize(option.getValue());
            } else if (option.getName().equals("XX:MaxPermSize")) {
                view.setMaxPermGenSize(option.getValue());
            } else if (option.getName().equals("JVM Options")) {
                view.setJVMCommandLineOpt(option.getValue());
            }
        }
    }
}
