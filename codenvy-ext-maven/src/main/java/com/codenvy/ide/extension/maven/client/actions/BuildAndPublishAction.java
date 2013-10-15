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
package com.codenvy.ide.extension.maven.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.maven.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.maven.client.BuilderResources;
import com.codenvy.ide.extension.maven.client.build.BuildProjectPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to build current project and get resulting artifact coordinates.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
@Singleton
public class BuildAndPublishAction extends Action {

    private final ResourceProvider      resourceProvider;
    private       BuildProjectPresenter presenter;

    @Inject
    public BuildAndPublishAction(BuildProjectPresenter presenter, BuilderResources resources,
                                 BuilderLocalizationConstant localizationConstant, ResourceProvider resourceProvider) {
        super(localizationConstant.buildAndPublishProjectControlText(),
              localizationConstant.buildAndPublishProjectControlDescription(), resources.buildAndPublish());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.buildProject(resourceProvider.getActiveProject(), true, false);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        boolean isEnabled = false;
        if (activeProject != null) {
            if (activeProject.getDescription().getNatures().contains("CodenvyExtension")) {
                e.getPresentation().setVisible(false);
            } else {
                isEnabled = true;
            }
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}
