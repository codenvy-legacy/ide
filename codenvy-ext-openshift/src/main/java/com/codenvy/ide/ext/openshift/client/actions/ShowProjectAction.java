/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
package com.codenvy.ide.ext.openshift.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.ext.openshift.client.project.ProjectPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ShowProjectAction extends Action {


    private final ProjectPresenter projectPresenter;
    private final ResourceProvider resourceProvider;

    @Inject
    public ShowProjectAction(ProjectPresenter projectPresenter, ResourceProvider resourceProvider, OpenShiftResources resources) {
        super("OpenShift", "Shows OpenShift project properties", resources.openShift());
        this.projectPresenter = projectPresenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        projectPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project project = resourceProvider.getActiveProject();
        e.getPresentation().setVisible(project != null && project.getPropertyValue("openshift-express-application") != null);
    }
}
