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
package com.codenvy.ide.ext.tutorials.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.tutorials.client.GuidePageController;
import com.codenvy.ide.ext.tutorials.client.TutorialsLocalizationConstant;
import com.codenvy.ide.ext.tutorials.client.TutorialsResources;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to open a tutorial guide.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ShowTutorialGuideAction extends Action {

    private AppContext           appContext;
    private GuidePageController  guidePageController;
    private AnalyticsEventLogger eventLogger;

    @Inject
    public ShowTutorialGuideAction(GuidePageController guidePageController, TutorialsResources resources,
                                   AppContext appContext,
                                   TutorialsLocalizationConstant localizationConstants,
                                   AnalyticsEventLogger eventLogger) {
        super(localizationConstants.showTutorialGuideActionText(),
              localizationConstants.showTutorialGuideActionDescription(), resources.guide());
        this.guidePageController = guidePageController;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Show tutorial");
        guidePageController.openTutorialGuide();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = appContext.getCurrentProject();
        if (activeProject != null) {
            e.getPresentation().setEnabledAndVisible(activeProject.getProjectTypeId().equals(Constants.TUTORIAL_ID));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
