/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.tutorials.client.action;

import com.codenvy.ide.api.logger.AnalyticsEventLogger;
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
