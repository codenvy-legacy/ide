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
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.ide.resources.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.DEFAULT_README_FILE_NAME;

/**
 * Controls a tutorial page state: can shows or hides it. Automatically shows a tutorial page when project has opened
 * and closes it when project has closed.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GuidePageController {
    private final ResourceProvider resourceProvider;
    private final WorkspaceAgent   workspaceAgent;
    private final GuidePage        guidePage;

    @Inject
    public GuidePageController(ResourceProvider resourceProvider, EventBus eventBus,
                               WorkspaceAgent workspaceAgent,
                               GuidePage guidePage) {
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.guidePage = guidePage;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (event.getProject() != null &&
                    event.getProject().getDescription().getProjectTypeId().equals(Constants.TUTORIAL_ID)) {
                    openTutorialPage();
                }
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (event.getProject() != null &&
                    event.getProject().getDescription().getProjectTypeId().equals(Constants.TUTORIAL_ID)) {
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
            workspaceAgent.openPart(guidePage, PartStackType.EDITING);
        }
    }

    /** Close tutorial description page. */
    public void closeTutorialPage() {
        workspaceAgent.removePart(guidePage);
    }

    private boolean isTutorialContainsGuide() {
        if (resourceProvider.getActiveProject() != null) {
            return resourceProvider.getActiveProject().findResourceByName(DEFAULT_README_FILE_NAME, File.TYPE) != null;
        }
        return false;
    }
}
