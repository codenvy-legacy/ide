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
package com.codenvy.ide.ext.java.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.CurrentProject;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.shared.Constants;

/** @author Evgen Vidolob */
public class UpdateDependencyAction extends Action {

    private final JavaExtension        javaExtension;
    private final AppContext           appContext;
    private final AnalyticsEventLogger eventLogger;
    private       BuildContext         buildContext;

    public UpdateDependencyAction(JavaExtension javaExtension, AppContext appContext,
                                  AnalyticsEventLogger eventLogger, JavaResources resources, BuildContext buildContext) {
        super("Update Dependencies", "Update Dependencies", null, resources.updateDependencies());
        this.javaExtension = javaExtension;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.buildContext = buildContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Update project dependencies");
        javaExtension.updateDependencies(appContext.getCurrentProject().getProjectDescription().getName());
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        if (buildContext.isBuilding()) {
            e.getPresentation().setEnabled(false);
            return;
        }
        CurrentProject activeProject = appContext.getCurrentProject();
        if (activeProject != null) {
            final String builder = activeProject.getAttributeValue(Constants.BUILDER_NAME);
            if ("maven".equals(builder)) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
