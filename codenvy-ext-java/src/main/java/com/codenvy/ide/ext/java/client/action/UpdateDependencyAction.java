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
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.shared.Constants;

/** @author Evgen Vidolob */
public class UpdateDependencyAction extends Action {

    private final JavaExtension        javaExtension;
    private final ResourceProvider     resourceProvider;
    private final AnalyticsEventLogger eventLogger;

    public UpdateDependencyAction(JavaExtension javaExtension, ResourceProvider resourceProvider,
                                  AnalyticsEventLogger eventLogger, JavaResources resources) {
        super("Update Dependencies", "Update Dependencies", null, resources.updateDependencies());
        this.javaExtension = javaExtension;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Update project dependencies");
        javaExtension.updateDependencies(resourceProvider.getActiveProject());
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
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
