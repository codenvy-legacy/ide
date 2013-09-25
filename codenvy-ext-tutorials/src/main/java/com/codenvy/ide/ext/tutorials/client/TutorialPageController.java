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
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.DEFAULT_README_FILE_NAME;
import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.TUTORIAL_PROJECT_TYPE;

/**
 * Controls a tutorial page state: can shows or hides it. Automatically shows a tutorial page when project has opened
 * and closes it when project has closed.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialPageController.java Sep 13, 2013 12:48:08 PM azatsarynnyy $
 */
@Singleton
public class TutorialPageController {
    private final ResourceProvider   resourceProvider;
    private final WorkspacePresenter workspacePresenter;
    private final TutorialGuidePage  tutorialGuidePage;

    @Inject
    public TutorialPageController(ResourceProvider resourceProvider, EventBus eventBus,
                                  Provider<WorkspacePresenter> workspaceProvider,
                                  TutorialGuidePage tutorialGuidePage) {
        this.resourceProvider = resourceProvider;
        this.workspacePresenter = workspaceProvider.get();
        this.tutorialGuidePage = tutorialGuidePage;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (event.getProject() != null && event.getProject().getDescription().getNatures().contains(TUTORIAL_PROJECT_TYPE)) {
                    openTutorialPage();
                }
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (event.getProject() != null && event.getProject().getDescription().getNatures().contains(TUTORIAL_PROJECT_TYPE)) {
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
        if (isTutorialContainsGuide()) {
            workspacePresenter.openPart(tutorialGuidePage, PartStackType.EDITING);
        }
    }

    /** Close tutorial description page. */
    public void closeTutorialPage() {
        workspacePresenter.removePart(tutorialGuidePage);
    }

    private boolean isTutorialContainsGuide() {
        return resourceProvider.getActiveProject().findResourceByName(DEFAULT_README_FILE_NAME, File.TYPE) != null;
    }
}
