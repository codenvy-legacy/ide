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
package org.eclipse.che.ide.extension.builder.client.actions;

import org.eclipse.che.ide.api.action.permits.ActionDenyAccessDialog;
import org.eclipse.che.ide.api.action.permits.ActionPermit;
import org.eclipse.che.ide.api.action.permits.Build;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.build.BuildContext;
import org.eclipse.che.ide.extension.builder.client.BuilderLocalizationConstant;
import org.eclipse.che.ide.extension.builder.client.BuilderResources;
import org.eclipse.che.ide.extension.builder.client.build.BuildController;

/**
 * Action to build current project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildAction extends ProjectAction {

    private final BuildController        buildController;
    private final AnalyticsEventLogger   eventLogger;
    private final BuildContext           buildContext;
    private final ActionPermit           buildActionPermit;
    private final ActionDenyAccessDialog buildActionDenyAccessDialog;

    @Inject
    public BuildAction(BuildController buildController,
                       BuilderResources resources,
                       BuilderLocalizationConstant localizationConstant,
                       AnalyticsEventLogger eventLogger,
                       BuildContext buildContext,
                       @Build ActionPermit buildActionPermit,
                       @Build ActionDenyAccessDialog buildActionDenyAccessDialog) {
        super(localizationConstant.buildProjectControlTitle(), localizationConstant.buildProjectControlDescription(), resources.build());
        this.buildController = buildController;
        this.eventLogger = eventLogger;
        this.buildContext = buildContext;
        this.buildActionPermit = buildActionPermit;
        this.buildActionDenyAccessDialog = buildActionDenyAccessDialog;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (buildActionPermit.isAllowed()) {
            buildController.buildActiveProject(true);
        } else {
            buildActionDenyAccessDialog.show();
        }
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(true);

        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            e.getPresentation().setEnabled(false);
        } else {
            e.getPresentation().setEnabled(currentProject.getBuilder() != null && !buildContext.isBuilding());
        }
    }
}
