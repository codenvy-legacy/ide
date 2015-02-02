/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projecttype.wizard.runnersPage;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard1.AbstractWizardPage;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.api.project.shared.Constants.BLANK_ID;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.CREATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.WIZARD_MODE_KEY;

/**
 * Project wizard page for configuring runner environment.
 *
 * @author Evgen Vidolob
 * @author Artem Zatsarynnyy
 */
public class RunnersPagePresenter extends AbstractWizardPage<ImportProject> implements RunnersPageView.ActionDelegate {

    private final ProjectWizardRegistry  projectWizardRegistry;
    private final RunnersPageView        view;
    private final RunnerServiceClient    runnerServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoFactory             dtoFactory;
    private final DialogFactory          dialogFactory;

    @Inject
    public RunnersPagePresenter(RunnersPageView view,
                                RunnerServiceClient runnerServiceClient,
                                DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                ProjectServiceClient projectServiceClient,
                                ProjectWizardRegistry projectWizardRegistry,
                                DtoFactory dtoFactory,
                                DialogFactory dialogFactory) {
        super();
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.projectWizardRegistry = projectWizardRegistry;
        this.dtoFactory = dtoFactory;
        this.dialogFactory = dialogFactory;
        view.setDelegate(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        requestRunnerEnvironments();
    }

    private void requestRunnerEnvironments() {
        final ProjectWizardMode wizardMode = ProjectWizardMode.parse(context.get(WIZARD_MODE_KEY));
        if (CREATE == wizardMode) {
            // wizard is opened for new project, so we haven't project-scoped environments
            requestSystemEnvironments();
            return;
        }

        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        projectServiceClient.getRunnerEnvironments(
                dataObject.getProject().getName(),
                new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
                    @Override
                    protected void onSuccess(RunnerEnvironmentTree environmentTree) {
                        if (!environmentTree.getLeaves().isEmpty() || !environmentTree.getNodes().isEmpty()) {
                            view.addRunner(environmentTree);
                        }
                        requestSystemEnvironments();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        dialogFactory.createMessageDialog("", exception.getMessage(), null).show();
                    }
                });
    }

    private void requestSystemEnvironments() {
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        runnerServiceClient.getRunners(new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree environmentTree) {
                final String category = projectWizardRegistry.getWizardCategory(dataObject.getProject().getType());
                if (category == null || BLANK_ID.equalsIgnoreCase(category)) {
                    view.addRunner(environmentTree);
                } else {
                    RunnerEnvironmentTree tree = dtoFactory.createDto(RunnerEnvironmentTree.class)
                                                           .withDisplayName(environmentTree.getDisplayName());
                    tree.addNode(environmentTree.getNode(category.toLowerCase()));
                    view.addRunner(tree);
                }
                updateView();
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog("", exception.getMessage(), null).show();
            }
        });
    }

    /** Updates view from data-object. */
    private void updateView() {
        final RunnersDescriptor runners = dataObject.getProject().getRunners();
        if (runners != null) {
            final String defaultRunner = runners.getDefault();
            view.selectRunnerEnvironment(defaultRunner);

            final RunnerConfiguration defaultRunnerConfig = runners.getConfigs().get(defaultRunner);
            if (defaultRunnerConfig != null) {
                view.setRecommendedMemorySize(defaultRunnerConfig.getRam());
            }
        }
    }

    @Override
    public void recommendedMemoryChanged() {
        final RunnersDescriptor runners = dataObject.getProject().getRunners();
        if (runners != null) {
            final String defaultRunner = runners.getDefault();
            final RunnerConfiguration defaultRunnerConf = runners.getConfigs().get(defaultRunner);
            if (defaultRunnerConf != null) {
                defaultRunnerConf.setRam(view.getRecommendedMemorySize());
            }
        }

        updateDelegate.updateControls();
    }

    @Override
    public void environmentSelected(@Nullable RunnerEnvironment environment) {
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

            dataObject.getProject().setRunners(runnersDescriptor);
            view.showRunnerDescriptions(environment.getDescription());
        } else {
            dataObject.getProject().setRunners(dtoFactory.createDto(RunnersDescriptor.class));
            view.showRunnerDescriptions("");
        }
    }
}
