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

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.event.ConfigureProjectEvent;

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
