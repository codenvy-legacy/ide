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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.TUTORIAL_PROJECT_TYPE;

/**
 * Controls a tutorial page state: can shows or hides it. Automatically shows tutorial page when project has opened and
 * closes it when project has closed.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialPageController.java Sep 13, 2013 12:48:08 PM azatsarynnyy $
 */
@Singleton
public class TutorialPageController {
    private final EventBus           eventBus;
    private final WorkspacePresenter workspacePresenter;
    private final TutorialPage       tutorialPage;

    @Inject
    public TutorialPageController(EventBus eventBus, Provider<WorkspacePresenter> workspaceProvider,
                                  TutorialPage tutorialPage) {
        this.eventBus = eventBus;
        this.workspacePresenter = workspaceProvider.get();
        this.tutorialPage = tutorialPage;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (event.getProject().getDescription().getNatures().contains(TUTORIAL_PROJECT_TYPE)) {
                    openTutorialPage();
                }
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (event.getProject().getDescription().getNatures().contains(TUTORIAL_PROJECT_TYPE)) {
                    closeTutorialPage();
                }
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
    }

    /** Open tutorial description page. */
    public void openTutorialPage() {
        workspacePresenter.openPart(tutorialPage, PartStackType.EDITING);
    }

    /** Close tutorial description page. */
    public void closeTutorialPage() {
        workspacePresenter.removePart(tutorialPage);
    }
}
