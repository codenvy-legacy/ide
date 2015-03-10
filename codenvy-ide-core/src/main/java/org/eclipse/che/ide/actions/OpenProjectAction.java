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
import org.eclipse.che.ide.openproject.OpenProjectPresenter;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;

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
        eventLogger.log(this);
        presenter.showDialog();
    }

    @Override
    public void update(ActionEvent e) {
        super.update(e);
    }
}
