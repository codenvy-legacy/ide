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
package com.codenvy.ide.ext.git.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.delete.DeleteRepositoryPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class DeleteRepositoryAction extends Action {
    private final DeleteRepositoryPresenter presenter;
    private final ResourceProvider          resourceProvider;
    private final AnalyticsEventLogger      eventLogger;

    @Inject
    public DeleteRepositoryAction(DeleteRepositoryPresenter presenter, ResourceProvider resourceProvider,
                                  GitResources resources,
                                  GitLocalizationConstant constant, AnalyticsEventLogger eventLogger) {
        super(constant.deleteControlTitle(), constant.deleteControlPrompt(), null, resources.deleteRepo());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Git delete repository");
        presenter.deleteRepository();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();

        e.getPresentation().setVisible(activeProject != null);

        if (activeProject != null) {
//            boolean isGitRepository = activeProject.getProperty(GIT_REPOSITORY_PROP) != null;
            e.getPresentation().setEnabled(true);
        }
    }
}