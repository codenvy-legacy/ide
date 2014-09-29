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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.wizard.SelectRunnerPageView.Target;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPagePresenter extends AbstractWizardPage implements SelectRunnerPageView.ActionDelegate {

    private final static String                   RUNNER_NAME_SPLITTER = "-";
    private SelectRunnerPageView                  view;
    private RunnerServiceClient                   runnerServiceClient;
    private DtoUnmarshallerFactory                dtoUnmarshallerFactory;
    private ProjectServiceClient                  projectServiceClient;
    private EventBus                              eventBus;
    private DtoFactory                            factory;
    private RunnerDescriptor                      runner;
    private String                                environmentId;

    /**
     * The structure of runners parts. Environment Technology - Target Environment - Specific Sub-Technology Example : java-webapp-default.
     * Map<Technology, Map<Target, Set<Sub-Technology>>.
     */
    private Map<String, Map<String, Set<String>>> runnersStructure;
    private StringMap<RunnerDescriptor>           runnersMap;


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
    public void storeOptions() {
        String recommendedMemorySize = view.getRecommendedMemorySize();
        int recommendedRam =
                             (!recommendedMemorySize.isEmpty() && isRecommendedMemoryCorrect()) ? (Integer.valueOf(recommendedMemorySize))
                                 : 0;
        wizardContext.putData(ProjectWizard.RECOMMENDED_RAM, recommendedRam);
    }

    @Override
    public void setUpdateDelegate(@NotNull Wizard.UpdateDelegate delegate) {
        super.setUpdateDelegate(delegate);
    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        if (wizardContext.getData(ProjectWizard.PROJECT) == null) {
            callback.onFailure(new IllegalStateException("Can't find project to set runner"));
            return;
        }

        if (runner == null) {
            callback.onSuccess();
            return;
        }
        storeOptions();

        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        project.setRunner(runner.getName());

        // Save recommended Ram and defaultRunnerEnvironment in projectDescriptor
        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.RUNNER_ENV_ID);
        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = project.getRunnerEnvironmentConfigurations();
        RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor = null;
        if (defaultRunnerEnvironment != null && runEnvConfigurations != null) {
            project.setDefaultRunnerEnvironment(defaultRunnerEnvironment);
            runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);

            if (runnerEnvironmentConfigurationDescriptor == null) {
                runnerEnvironmentConfigurationDescriptor = factory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
            }
            runnerEnvironmentConfigurationDescriptor.setRecommendedMemorySize(wizardContext.getData(ProjectWizard.RECOMMENDED_RAM));
            runEnvConfigurations.put(defaultRunnerEnvironment, runnerEnvironmentConfigurationDescriptor);
            project.setRunnerEnvironmentConfigurations(runEnvConfigurations);
        }

        projectServiceClient.updateProject(project.getPath(),
                                           project,
                                           new AsyncRequestCallback<ProjectDescriptor>(
                                                                                       dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                               @Override
                                               protected void onSuccess(ProjectDescriptor result) {
                                                   eventBus.fireEvent(new OpenProjectEvent(result.getName()));
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
        view.hideTargets();
        view.hideSubTechnologies();
        view.setEnvironmentsEnableState(false);
        requestRunners();
    }

    private void requestRunners() {
        runnerServiceClient.getRunners(
                           new AsyncRequestCallback<Array<RunnerDescriptor>>(
                                                                             dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
                               @Override
                               protected void onSuccess(Array<RunnerDescriptor> result) {
                                   parseRunners(result);
                                   Array<String> technologies = Collections.createArray(runnersStructure.keySet());
                                   technologies.sort(new Comparator<String>() {
                                       @Override
                                       public int compare(String o1, String o2) {
                                           return o1.compareTo(o2);
                                       }
                                   });

                                   view.showTechnologies(technologies);
                                   if (technologies.size() > 0) {
                                       view.selectTechnology(technologies.get(0));
                                   }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   Log.error(SelectRunnerPagePresenter.class, "Can't receive runners info", exception);
                               }
                           });
    }

    /**
     * Parsers the runner's name into a structure: Technology-Target-Sub-Technology. The result is the following map - Map<Technology,
     * Map<Target, Set<Sub-Technology>>. Sample technologies - Java, Python, PHP... Sample targets : Console, WebApp, Standalone, Mobile.
     * Sample sub technologies : play, android, default ...
     * 
     * @param runners
     */
    private void parseRunners(Array<RunnerDescriptor> runners) {
        runnersStructure = new HashMap<String, Map<String, Set<String>>>();
        runnersMap = Collections.createStringMap();
        for (RunnerDescriptor runner : runners.asIterable()) {
            runnersMap.put(runner.getName(), runner);

            String[] parts = runner.getName().split(RUNNER_NAME_SPLITTER);
            String tech = (parts.length > 3) ? runner.getName() : parts[0];
            String target = (parts.length > 1 && parts.length <= 3) ? parts[1] : null;
            String subtech = (parts.length == 3) ? parts[2] : null;

            Map<String, Set<String>> targetMap = runnersStructure.get(tech);
            if (targetMap == null) {
                targetMap = new HashMap<String, Set<String>>();
                runnersStructure.put(tech, targetMap);
            }

            if (target != null) {
                Set<String> subtechs = targetMap.get(target);
                if (subtechs == null) {
                    subtechs = new HashSet<String>();
                    targetMap.put(target, subtechs);
                }

                if (subtech != null) {
                    subtechs.add(subtech);
                }
            }
        }
    }

    /**
     * Perform action on runner selected.
     * 
     * @param runner selected runner
     */
    public void runnerSelected(RunnerDescriptor runner) {
        this.runner = runner;
        wizardContext.putData(ProjectWizard.RUNNER_NAME, runner.getName());
        delegate.updateControls();
        view.showEnvironments(runner.getEnvironments());
        selectEnvironment();
    }

    private void selectEnvironment() {
        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.RUNNER_ENV_ID);
        view.setSelectedEnvironment(defaultRunnerEnvironment);
    }

    @Override
    public void runnerEnvironmentSelected(String environmentId) {
        wizardContext.putData(ProjectWizard.RUNNER_ENV_ID, environmentId);
        this.environmentId = environmentId;
        setRecommendedMemorySize();
    }

    private void setRecommendedMemorySize() {
        int recommendedMemorySize = 0;
        ProjectDescriptor projectDescriptor = wizardContext.getData(ProjectWizard.PROJECT);
        String runnerName = wizardContext.getData(ProjectWizard.RUNNER_NAME);

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
        } else
            view.setRecommendedMemorySize("");
    }

    @Override
    public void recommendedMemoryChanged() {
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

    /** {@inheritDoc} */
    @Override
    public void targetSelected(Target target) {
        String technology = view.getSelectedTechnology();
        if (runnersStructure.containsKey(technology)) {
            Map<String, Set<String>> targets = runnersStructure.get(technology);
            if (targets.containsKey(target.getId())) {
                Set<String> subTechnologies = targets.get(target.getId());
                // No sub technologies of the pair technology+target - consider the runner is selected:
                if (subTechnologies == null || subTechnologies.isEmpty()) {
                    String runnerName = view.getSelectedTechnology() + RUNNER_NAME_SPLITTER + target.getId();
                    if (runnersMap.containsKey(runnerName)) {
                        runnerSelected(runnersMap.get(runnerName));
                    }
                } else {
                    view.showSubTechnologies(subTechnologies);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void technologySelected(String technology) {
        view.hideTargets();
        view.hideSubTechnologies();
        view.setEnvironmentsEnableState(false);

        if (runnersStructure.containsKey(technology)) {
            Map<String, Set<String>> targets = runnersStructure.get(technology);
            Array<Target> targetsToDisplay = Collections.createArray();

            if (!targets.keySet().isEmpty()) {
                for (String target : targets.keySet()) {
                    Target t = Target.fromString(target);
                    targetsToDisplay.add(t);
                }
            } else {
                // No target - consider the runner is selected:
                if (runnersMap.containsKey(technology)) {
                    runnerSelected(runnersMap.get(technology));
                }
            }
            view.displayTargets(targetsToDisplay);

            if (targetsToDisplay.size() == 1) {
                view.selectTarget(targetsToDisplay.get(0));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void subTechnologySelected(String subTechnology) {
        String runnerName =
                            view.getSelectedTechnology() + RUNNER_NAME_SPLITTER + view.getSelectedTarget().getId() + RUNNER_NAME_SPLITTER
                                + subTechnology;
        if (runnersMap.containsKey(runnerName)) {
            runnerSelected(runnersMap.get(runnerName));
        }
    }
}
