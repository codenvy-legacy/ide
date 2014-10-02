/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.extension.runner.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentConfigurationDescriptor;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPagePresenter extends AbstractWizardPage implements SelectRunnerPageView.ActionDelegate {

    private SelectRunnerPageView   view;
    private RunnerServiceClient    runnerServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private ProjectServiceClient   projectServiceClient;
    private EventBus               eventBus;
    private DtoFactory             factory;
    private RunnerDescriptor       runner;
    private String                 environmentId;
    private Comparator<RunnerDescriptor> comparator = new Comparator<RunnerDescriptor>() {
        @Override
        public int compare(RunnerDescriptor o1, RunnerDescriptor o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    /**
     * Create wizard page
     */
    @Inject
    public SelectRunnerPagePresenter(SelectRunnerPageView view,
                                     RunnerServiceClient runnerServiceClient,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectServiceClient projectServiceClient,
                                     EventBus eventBus,
                                     DtoFactory factory) {
        super("Select Runner", null);
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.factory = factory;
        view.setDelegate(this);
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return isRecommendedMemoryCorrect();
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }


    @Override
    public void setUpdateDelegate(@Nonnull Wizard.UpdateDelegate delegate) {
        super.setUpdateDelegate(delegate);
    }


    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        requestRunners();
    }

    private void requestRunners() {
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<Array<RunnerDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<RunnerDescriptor> result) {
                        List<RunnerDescriptor> list = new ArrayList<>(result.size());
                        for (RunnerDescriptor runnerDescriptor : result.asIterable()) {
                            list.add(runnerDescriptor);
                        }
                        Collections.sort(list, comparator);
                        view.showRunners(list);
                        selectRunner();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(SelectRunnerPagePresenter.class, "Can't receive runners info", exception);
                    }
                });
    }

    private void selectRunner() {
        String runnerName = wizardContext.getData(ProjectWizard.PROJECT).getRunner();
        if (runnerName != null) {
            view.selectRunner(runnerName);
        }
    }

    @Override
    public void runnerSelected(RunnerDescriptor runner) {
        this.runner = runner;
        delegate.updateControls();
        wizardContext.getData(ProjectWizard.PROJECT).setRunner(runner.getName());
        selectEnvironment();
    }

    private void selectEnvironment() {
        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.PROJECT).getDefaultRunnerEnvironment();
        view.setSelectedEnvironment(defaultRunnerEnvironment);
    }

    @Override
    public void runnerEnvironmentSelected(String environmentId) {
        wizardContext.getData(ProjectWizard.PROJECT).setDefaultRunnerEnvironment(environmentId);
        this.environmentId = environmentId;
        setRecommendedMemorySize();
    }

    private void setRecommendedMemorySize() {
        int recommendedMemorySize = 0;
        ProjectDescriptor projectDescriptor = wizardContext.getData(ProjectWizard.PROJECT);
        String runnerName = runner.getName();

        if (projectDescriptor != null && runnerName.equals(projectDescriptor.getRunner())) {
            Map<String, RunnerEnvironmentConfigurationDescriptor> configurations = projectDescriptor.getRunnerEnvironmentConfigurations();
            if (environmentId != null && configurations != null && configurations.containsKey(environmentId)) {
                RunnerEnvironmentConfigurationDescriptor runEnvConfigDescriptor = configurations.get(environmentId);
                if (runEnvConfigDescriptor != null) {
                    recommendedMemorySize = runEnvConfigDescriptor.getRecommendedMemorySize();
                }
            }
        }

        if (recommendedMemorySize > 0) {
            view.setRecommendedMemorySize(String.valueOf(recommendedMemorySize));

            Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = projectDescriptor.getRunnerEnvironmentConfigurations();

            RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor;
            if (environmentId != null && runEnvConfigurations != null) {
                projectDescriptor.setDefaultRunnerEnvironment(environmentId);
                runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(environmentId);

                if (runnerEnvironmentConfigurationDescriptor == null) {
                    runnerEnvironmentConfigurationDescriptor = factory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
                }
                runnerEnvironmentConfigurationDescriptor.setRecommendedMemorySize(recommendedMemorySize);
                runEnvConfigurations.put(environmentId, runnerEnvironmentConfigurationDescriptor);
                projectDescriptor.setRunnerEnvironmentConfigurations(runEnvConfigurations);
            } else {
                view.setRecommendedMemorySize("");
            }
        }
    }

        @Override
        public void recommendedMemoryChanged () {
            delegate.updateControls();
        }

    private boolean isRecommendedMemoryCorrect() {
        int recommendedMemory;

        if (view.getRecommendedMemorySize().isEmpty()) {
            return true;
        }
        try {
            recommendedMemory = Integer.parseInt(view.getRecommendedMemorySize());
            if (recommendedMemory < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
