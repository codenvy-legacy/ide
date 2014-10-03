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
import com.codenvy.ide.about.AboutLocalizationConstant;
import com.codenvy.ide.about.AboutPresenter;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.google.inject.Inject;

/**
 * Action for showing About application information.
 *
 * @author Ann Shumilova
 */
public class ShowAboutAction extends Action {

    private final AboutPresenter       presenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public ShowAboutAction(AboutPresenter presenter, AboutLocalizationConstant locale, AnalyticsEventLogger eventLogger,
                           Resources resources) {
        super(locale.aboutControlTitle(), "Show about application", null, resources.about());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.showAbout();
    }

}
