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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.tutorials.client.TutorialPageController;
import com.codenvy.ide.ext.tutorials.client.TutorialsLocalizationConstant;
import com.codenvy.ide.ext.tutorials.client.TutorialsResources;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.TUTORIAL_PROJECT_TYPE;

/**
 * Action to open tutorial's readme file.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowTutorialPageAction.java Sep 16, 2013 1:58:47 PM azatsarynnyy $
 */
@Singleton
public class ShowTutorialPageAction extends Action {

    private final ResourceProvider       resourceProvider;
    private       TutorialPageController tutorialPageController;

    @Inject
    public ShowTutorialPageAction(TutorialPageController tutorialPageController, TutorialsResources resources,
                                  ResourceProvider resourceProvider,
                                  TutorialsLocalizationConstant localizationConstants) {
        super(localizationConstants.showTutorialPageActionText(),
              localizationConstants.showTutorialPageActionDescription(), resources.file());
        this.tutorialPageController = tutorialPageController;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        tutorialPageController.openTutorialPage();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            e.getPresentation()
             .setEnabledAndVisible(activeProject.getDescription().getNatures().contains(TUTORIAL_PROJECT_TYPE));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
