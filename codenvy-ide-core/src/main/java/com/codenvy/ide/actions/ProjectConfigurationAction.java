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
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.ConfigureCurrentProjectEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Call Project wizard to change project type
 *
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectConfigurationAction extends Action {

    private final AnalyticsEventLogger eventLogger;
    private final EventBus             eventBus;
    private final AppContext           appContext;

    @Inject
    public ProjectConfigurationAction(AppContext appContext,
                                      CoreLocalizationConstant localization,
                                      AnalyticsEventLogger eventLogger,
                                      Resources resources,
                                      EventBus eventBus) {
        super(localization.actionProjectConfigurationDescription(), localization.actionProjectConfigurationTitle(), null,
              resources.projectConfiguration());
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.eventBus = eventBus;
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(appContext.getCurrentProject() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        eventBus.fireEvent(new ConfigureCurrentProjectEvent());
    }
}
