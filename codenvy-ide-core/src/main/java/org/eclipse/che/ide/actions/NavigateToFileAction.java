/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.navigation.NavigateToFilePresenter;
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
    private final AppContext              appContext;
    private final AnalyticsEventLogger    eventLogger;

    @Inject
    public NavigateToFileAction(NavigateToFilePresenter presenter,
                                AppContext appContext,
                                AnalyticsEventLogger eventLogger, Resources resources) {
        super("Navigate to File", "Navigate to file", null, resources.navigateToFile());
        this.presenter = presenter;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }


    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(appContext.getCurrentProject() != null);
    }
}
