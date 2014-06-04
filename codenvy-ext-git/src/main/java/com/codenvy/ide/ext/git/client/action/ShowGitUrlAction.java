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
package com.codenvy.ide.ext.git.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.url.ShowProjectGitReadOnlyUrlPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class ShowGitUrlAction extends Action {
    private final ShowProjectGitReadOnlyUrlPresenter presenter;
    private final ResourceProvider                   resourceProvider;
    private final AnalyticsEventLogger               eventLogger;

    @Inject
    public ShowGitUrlAction(ShowProjectGitReadOnlyUrlPresenter presenter, ResourceProvider resourceProvider,
                            GitResources resources,
                            GitLocalizationConstant constant, AnalyticsEventLogger eventLogger) {
        super(constant.projectReadOnlyGitUrlPrompt(), constant.projectReadOnlyGitUrlPrompt(), null,
              resources.projectReadOnlyGitUrl());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Git show git url");
        presenter.showDialog();
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