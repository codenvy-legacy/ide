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
package com.codenvy.ide.extension.builder.client.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.extension.builder.client.build.BuildController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to build current project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildAction extends Action {

    private final AppContext           appContext;
    private final BuildController      buildController;
    private final AnalyticsEventLogger eventLogger;
    private       BuildContext         buildContext;

    @Inject
    public BuildAction(BuildController buildController, BuilderResources resources,
                       BuilderLocalizationConstant localizationConstant, AppContext appContext,
                       AnalyticsEventLogger eventLogger, BuildContext buildContext) {
        super(localizationConstant.buildProjectControlTitle(),
              localizationConstant.buildProjectControlDescription(), null, resources.build());
        this.buildController = buildController;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.buildContext = buildContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Build project");
        buildController.buildActiveProject(true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        if (buildContext.isBuilding()) {
            e.getPresentation().setEnabled(false);
            e.getPresentation().setVisible(true);
            return;
        }
        e.getPresentation().setEnabledAndVisible(appContext.getCurrentProject() != null);
    }
}
