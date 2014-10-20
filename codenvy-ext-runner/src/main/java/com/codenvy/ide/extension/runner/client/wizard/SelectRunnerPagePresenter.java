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
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPagePresenter extends AbstractWizardPage implements SelectRunnerPageView.ActionDelegate {

    private SelectRunnerPageView   view;
    private RunnerServiceClient    runnerServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private ProjectServiceClient   projectServiceClient;
    private DtoFactory             dtoFactory;

    /** Create wizard page. */
    @Inject
    public SelectRunnerPagePresenter(SelectRunnerPageView view,
                                     RunnerServiceClient runnerServiceClient,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectServiceClient projectServiceClient,
                                     DtoFactory dtoFactory) {
        super("Select Runner", null);
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.dtoFactory = dtoFactory;
        view.setDelegate(this);
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return true;
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
        requestRunnerEnvironments();
    }

    private void requestRunnerEnvironments() {
        ProjectDescriptor projectForUpdate = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);
        if (projectForUpdate == null) {
            // wizard is opened for new project, so we haven't project-scoped environments
            requestSystemEnvironments();
            return;
        }

        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        projectServiceClient.getRunnerEnvironments(projectForUpdate.getPath(), new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree result) {
                if (!result.getLeaves().isEmpty() || !result.getNodes().isEmpty()) {
                    view.addRunner(result);
                }
                requestSystemEnvironments();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(SelectRunnerPagePresenter.class, "Can't get project-scoped runner environments", exception);
            }
        });
    }

    private void requestSystemEnvironments() {
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        runnerServiceClient.getRunners(new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree result) {
                view.addRunner(result);
                selectRunner();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(SelectRunnerPagePresenter.class, "Can't receive runners info", exception);
            }
        });
    }

    private void selectRunner() {
        ProjectDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (descriptor.getRunners() != null) {
            RunnersDescriptor runners = descriptor.getRunners();
            view.selectRunnerEnvironment(runners.getDefault());
            final RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
            if (runnerConfiguration != null) {
                view.setRecommendedMemorySize(runnerConfiguration.getRam());
            }
        }
    }

    @Override
    public void recommendedMemoryChanged() {
        ProjectDescriptor projectDescriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (projectDescriptor.getRunners() != null) {
            String defaultRunner = projectDescriptor.getRunners().getDefault();
            RunnerConfiguration defaultRunnerConf = projectDescriptor.getRunners().getConfigs().get(defaultRunner);
            if (defaultRunnerConf != null) {
                defaultRunnerConf.setRam(view.getRecommendedMemorySize());
            }
        }

        delegate.updateControls();
    }

    @Override
    public void environmentSelected(@Nullable RunnerEnvironment environment) {
        ProjectDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT);

        if (environment != null) {
            RunnersDescriptor runnersDescriptor = dtoFactory.createDto(RunnersDescriptor.class);
            runnersDescriptor.setDefault(environment.getId());
            RunnerConfiguration runnerConfiguration = dtoFactory.createDto(RunnerConfiguration.class);
            runnerConfiguration.setOptions(environment.getOptions());
            runnerConfiguration.setVariables(environment.getVariables());
            Map<String, RunnerConfiguration> configurations = new HashMap<>();
            configurations.put(environment.getId(), runnerConfiguration);
            runnersDescriptor.setConfigs(configurations);

            runnerConfiguration.setRam(view.getRecommendedMemorySize());

            descriptor.setRunners(runnersDescriptor);
            view.showRunnerDescriptions(environment.getDescription());
        } else {
            descriptor.setRunners(null);
            view.showRunnerDescriptions("");
        }
    }
}
