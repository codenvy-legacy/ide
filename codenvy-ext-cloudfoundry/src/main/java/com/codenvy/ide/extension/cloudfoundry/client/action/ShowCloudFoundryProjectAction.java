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
package com.codenvy.ide.extension.cloudfoundry.client.action;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ShowCloudFoundryProjectAction extends Action {

    private final CloudFoundryProjectPresenter presenter;
    private final CloudFoundryResources        resources;
    private       ResourceProvider             resourceProvider;

    @Inject
    public ShowCloudFoundryProjectAction(CloudFoundryProjectPresenter presenter, CloudFoundryResources resources,
                                         ResourceProvider resourceProvider) {
        super("CloudFoudry", "Shows CloudFoundry project properties", resources.cloudFoundry());
        this.presenter = presenter;
        this.resources = resources;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog(CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY);
    }

    @Override
    public void update(ActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = resourceProvider.getActiveProject();
        presentation.setVisible(project != null && project.getProperty("cloudfoundry-application") != null);
    }
}
