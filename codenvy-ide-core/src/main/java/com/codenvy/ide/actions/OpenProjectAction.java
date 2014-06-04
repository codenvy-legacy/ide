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
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.openproject.OpenProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class OpenProjectAction extends Action {

    private final OpenProjectPresenter presenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public OpenProjectAction(OpenProjectPresenter presenter, AnalyticsEventLogger eventLogger, Resources resources) {
        super("Open Project", "Open project", null, resources.openProject());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Open project");
        presenter.showDialog();
    }

    @Override
    public void update(ActionEvent e) {
        super.update(e);
    }
}
