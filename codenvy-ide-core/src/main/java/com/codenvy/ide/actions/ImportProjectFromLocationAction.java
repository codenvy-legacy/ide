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
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.importproject.ImportProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Import project Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class ImportProjectFromLocationAction extends Action {

    private final ImportProjectPresenter presenter;
    private final AnalyticsEventLogger   eventLogger;

    @Inject
    public ImportProjectFromLocationAction(ImportProjectPresenter presenter,
                                           CoreLocalizationConstant locale,
                                           AnalyticsEventLogger eventLogger,
                                           Resources resources) {
        super(locale.importProjectName(), locale.importProjectDescription(), null, resources.importProject());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        eventLogger.log("IDE: Import project");
        presenter.showDialog();
    }
}
