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
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
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
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowTutorialGuideAction.java Sep 16, 2013 1:58:47 PM azatsarynnyy $
 */
@Singleton
public class ShowTutorialGuideAction extends Action {

    private final ResourceProvider     resourceProvider;
    private final GuidePageController  guidePageController;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public ShowTutorialGuideAction(GuidePageController guidePageController, TutorialsResources resources,
                                   ResourceProvider resourceProvider,
                                   TutorialsLocalizationConstant localizationConstants,
                                   AnalyticsEventLogger eventLogger) {
        super(localizationConstants.showTutorialGuideActionText(),
              localizationConstants.showTutorialGuideActionDescription(), resources.guide());
        this.guidePageController = guidePageController;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Show tutorial");
        guidePageController.openTutorialPage();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            e.getPresentation()
             .setEnabledAndVisible(activeProject.getDescription().getProjectTypeId().equals(Constants.TUTORIAL_ID));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
