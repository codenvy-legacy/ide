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
package com.codenvy.ide.extension.maven.client.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.maven.client.MavenLocalizationConstant;
import com.codenvy.ide.extension.maven.client.MavenResources;
import com.codenvy.ide.extension.maven.client.build.MavenBuilderPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to build current project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomBuildAction extends Action {

    private final AppContext            appContext;
    private final MavenBuilderPresenter presenter;
    private final AnalyticsEventLogger  eventLogger;
    private       BuildContext          buildContext;

    @Inject
    public CustomBuildAction(MavenBuilderPresenter presenter,
                             MavenResources resources,
                             MavenLocalizationConstant localizationConstant,
                             AppContext appContext,
                             AnalyticsEventLogger eventLogger,
                             BuildContext buildContext) {
        super(localizationConstant.buildProjectControlTitle(),
              localizationConstant.buildProjectControlDescription(), null, resources.build());
        this.presenter = presenter;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.buildContext = buildContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Build project with Maven parameter");
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = appContext.getCurrentProject();
        if (activeProject != null) {
            final String builder = activeProject.getAttributes().get("builder.name").get(0);
            if ("maven".equals(builder)) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
        if (buildContext.isBuilding()) {
            e.getPresentation().setEnabled(false);
        }
    }
}
