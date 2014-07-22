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
package com.codenvy.ide.ext.java.jdi.client.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.resources.ProjectsManager;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.extension.runner.client.ProjectRunCallback;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

/**
 * Action to run project on runner in debug mode.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DebugAction extends Action {

    private final RunnerController     runnerController;
    private final DebuggerPresenter    debuggerPresenter;
    private final ProjectsManager      projectsManager;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public DebugAction(RunnerController runnerController,
                       DebuggerPresenter debuggerPresenter,
                       JavaRuntimeResources resources,
                       ProjectsManager projectsManager,
                       JavaRuntimeLocalizationConstant localizationConstants,
                       AnalyticsEventLogger eventLogger) {
        super(localizationConstants.debugAppActionText(), localizationConstants.debugAppActionDescription(), null, resources.debug());
        this.runnerController = runnerController;
        this.debuggerPresenter = debuggerPresenter;
        this.projectsManager = projectsManager;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Debug application");
        runnerController.runActiveProject(true, new ProjectRunCallback() {
            @Override
            public void onRun(ApplicationProcessDescriptor appDescriptor, ProjectDescriptor project) {
                debuggerPresenter.attachDebugger(appDescriptor, project);
            }
        }, true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = projectsManager.getActiveProject();
        if (activeProject != null) {
            final String projectTypeId = activeProject.getProjectTypeId();
            Map<String, List<String>> attributes = activeProject.getAttributes();
            List<String> packaging = attributes.get(MavenAttributes.MAVEN_PACKAGING);
            if (packaging != null) {
                e.getPresentation().setVisible("war".equals(packaging.get(0)) ||
                                               projectTypeId.equals(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID));
                e.getPresentation().setEnabled(!runnerController.isAnyAppRunning());
                return;
            }
        }
        e.getPresentation().setEnabledAndVisible(false);
    }
}
