/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
