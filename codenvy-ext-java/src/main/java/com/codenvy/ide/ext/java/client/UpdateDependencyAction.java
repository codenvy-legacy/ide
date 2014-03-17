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
package com.codenvy.ide.ext.java.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.resources.model.Project;

/** @author Evgen Vidolob */
public class UpdateDependencyAction extends Action {

    private JavaExtension    javaExtension;
    private ResourceProvider resourceProvider;

    public UpdateDependencyAction(JavaExtension javaExtension, ResourceProvider resourceProvider) {
        super("Update dependencies", "Update dependencies", null);
        this.javaExtension = javaExtension;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        javaExtension.updateDependencies(resourceProvider.getActiveProject());
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            // TODO: read attribute value
            // it doesn't work now cause builder.name is calculated attribute
            final String builder = (String)activeProject.getAttributeValue("builder.name");
            if ("maven".equals(builder)) {
                e.getPresentation().setVisible(true);
                e.getPresentation().setEnabled(true);
            } else {
                e.getPresentation().setVisible(false);
                e.getPresentation().setEnabled(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
