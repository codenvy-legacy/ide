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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.navigation.NavigateToFilePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action for finding file by name and opening it.
 *
 * @author Ann Shumilova
 */
@Singleton
public class NavigateToFileAction extends Action {

    private final NavigateToFilePresenter presenter;
    private final ResourceProvider        resourceProvider;
    private final AnalyticsEventLogger    eventLogger;

    @Inject
    public NavigateToFileAction(NavigateToFilePresenter presenter,
                                ResourceProvider resourceProvider,
                                AnalyticsEventLogger eventLogger, Resources resources) {
        super("Navigate to File", "Navigate to file", null, resources.navigateToFile());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }


    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Navigate to file");
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        e.getPresentation().setEnabled(activeProject != null);
    }
}
