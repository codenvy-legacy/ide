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
    private JsonArray<ConfigurationOption> configuration;

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

        for (int i = 0; i < configuration.size(); i++) {
            ConfigurationOption option = configuration.get(i);

            if ("Xms".equals(option.getName()) && view.isInitialHeapSizeModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("Xms");
                dtoOption.setValue(view.getInitialHeapSize());
                options.add(dtoOption);
            }

            if ("Xmx".equals(option.getName()) && view.isMaxHeapSizeModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("Xmx");
                dtoOption.setValue(view.getMaxHeapSize());
                options.add(dtoOption);
            }

            if ("XX:MaxPermSize".equals(option.getName()) && view.isMaxPermGenSizeModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("XX:MaxPermSize");
                dtoOption.setValue(view.getMaxPermGenSize());
                options.add(dtoOption);
            }

            if ("JVM Options".equals(option.getName()) && view.isJVMCommandLineOptModified()) {
                DtoClientImpls.ConfigurationOptionImpl dtoOption = DtoClientImpls.ConfigurationOptionImpl.make();
                dtoOption.setNamespace(option.getNamespace());
                dtoOption.setName("JVM Options");
                dtoOption.setValue(view.getJVMCommandLineOpt());
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
