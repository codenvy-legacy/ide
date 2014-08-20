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
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
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
    private DtoFactory             factory;
    private RunnerDescriptor       runner;
    private String                 environmentId;
    private Comparator<RunnerDescriptor> comparator = new Comparator<RunnerDescriptor>() {
        @Override
        public int compare(RunnerDescriptor o1, RunnerDescriptor o2) {
            return o1.getDescription().compareToIgnoreCase(o2.getDescription());
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
                                     DtoFactory factory) {
        super("Select Runner", null);
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
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
        return true;
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void setUpdateDelegate(@NotNull Wizard.UpdateDelegate delegate) {
        super.setUpdateDelegate(delegate);
        selectRunner();
    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        if (wizardContext.getData(ProjectWizard.PROJECT) == null) {
            callback.onFailure(new IllegalStateException("Can't find project to set runner"));
            return;
        }

        if(runner == null){
            callback.onSuccess();
            return;
        }
        Project project = wizardContext.getData(ProjectWizard.PROJECT);

        final ProjectDescriptor projectDescriptor = factory.createDto(ProjectDescriptor.class);

        Map<String, List<String>> attributes = project.getAttributes();
        attributes.put(Constants.RUNNER_NAME, Arrays.asList(runner.getName()));
        if(environmentId != null) {
            attributes.put(Constants.RUNNER_ENV_ID, Arrays.asList(environmentId));
        } else {
            attributes.remove(Constants.RUNNER_ENV_ID);
        }

        projectDescriptor.setAttributes(attributes);
        projectDescriptor.setVisibility(project.getVisibility());
        projectDescriptor.setProjectTypeId(project.getDescription().getProjectTypeId());
        projectServiceClient.updateProject(project.getPath(),projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
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
        //TODO:temporary comment this method
//        Log.info(SelectRunnerPagePresenter.class, "select runner" + wizardContext.getData(ProjectWizard.RUNNER_NAME));
//        if (wizardContext.getData(ProjectWizard.RUNNER_NAME) != null) {
//            view.selectRunner(wizardContext.getData(ProjectWizard.RUNNER_NAME));
//        }
    }

    @Override
    public void runnerSelected(RunnerDescriptor runner) {
        this.runner = runner;
        delegate.updateControls();
        Log.info(SelectRunnerPagePresenter.class, "runner selected" + runner.getName());
        wizardContext.putData(ProjectWizard.RUNNER_NAME, runner.getName());
    }

    @Override
    public void runnerEnvironmentSelected(String environmentId) {
        wizardContext.putData(ProjectWizard.RUNNER_ENV_ID, environmentId);
        this.environmentId = environmentId;
    }
}
