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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.event.ConfigureProjectEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Call Project wizard to change project type
 *
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectConfigurationAction extends ProjectAction {

    private final AnalyticsEventLogger eventLogger;
    private final EventBus             eventBus;

    @Inject
    public ProjectConfigurationAction(CoreLocalizationConstant localization,
                                      AnalyticsEventLogger eventLogger,
                                      Resources resources,
                                      EventBus eventBus) {
        super(localization.actionProjectConfigurationDescription(), localization.actionProjectConfigurationTitle(),
              resources.projectConfiguration());
        this.eventLogger = eventLogger;
        this.eventBus = eventBus;
    }

    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (appContext.getCurrentProject() == null) {
            return;
        }

        eventLogger.log(this);
        eventBus.fireEvent(new ConfigureProjectEvent(appContext.getCurrentProject().getRootProject()));
    }
}
