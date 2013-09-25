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
public class ContainerTabPainPresenter implements Presenter, ContainerTabPainView.ActionDelegate, HasConfigurationProperty {
    private ContainerTabPainView           view;
    private JsonArray<ConfigurationOption> configuration;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ContainerTabPainPresenter(ContainerTabPainView view) {
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

    /** {@inheritDoc} */
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
