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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.ide.api.resources.model.File;
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
