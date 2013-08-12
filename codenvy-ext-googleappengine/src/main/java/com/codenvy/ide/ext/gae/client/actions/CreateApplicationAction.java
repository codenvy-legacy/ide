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
package com.codenvy.ide.ext.gae.client.actions;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.gae.client.GAEExtension;
import com.codenvy.ide.ext.gae.client.GAEResources;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action for "PaaS/Google App Engine/Create Application..." to allow user to create application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationAction extends Action {
    private CreateApplicationPresenter presenter;
    private ResourceProvider           resourceProvider;

    /**
     * Constructor for action.
     */
    @Inject
    public CreateApplicationAction(CreateApplicationPresenter presenter, GAEResources resources,
                                   ResourceProvider resourceProvider) {
        super("Create application...", "Create application on GAE.", resources.createApplicationConrtol());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        @NotNull
        Project project = resourceProvider.getActiveProject();

        presenter.showDialog(project);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project project = resourceProvider.getActiveProject();

        e.getPresentation().setEnabled(GAEExtension.isAppEngineProject(project));
    }
}
