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
package com.codenvy.ide.ext.cloudbees.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesResources;
import com.codenvy.ide.ext.cloudbees.client.project.CloudBeesProjectPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ShowCloudBeesProjectAction extends Action {

    private CloudBeesProjectPresenter presenter;
    private ResourceProvider          resourceProvider;

    @Inject
    public ShowCloudBeesProjectAction(CloudBeesProjectPresenter presenter, CloudBeesResources resources,
                                      ResourceProvider resourceProvider) {
        super("CloudBees", "Shows CloudBees project properties", resources.cloudBees());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }

    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        e.getPresentation().setVisible(activeProject != null && activeProject.getProperty("cloudbees-application") != null);
    }
}

