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
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to clear Builder console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ClearConsoleAction extends Action {

    private       BuilderConsolePresenter presenter;
    private       AppContext              appContext;
    private final AnalyticsEventLogger    eventLogger;

    @Inject
    public ClearConsoleAction(BuilderConsolePresenter presenter,
                              AppContext appContext,
                              BuilderResources resources,
                              BuilderLocalizationConstant localizationConstant,
                              AnalyticsEventLogger eventLogger) {
        super(localizationConstant.clearConsoleControlTitle(), localizationConstant.clearConsoleControlDescription(), null,
              resources.clear());
        this.presenter = presenter;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(appContext.getCurrentProject() != null);
    }
}
