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
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Controls a tutorial page state: shows or hides it.
 * Automatically shows a tutorial page when project opening and closes page when project closing.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GuidePageController {
    private WorkspaceAgent workspaceAgent;
    private GuidePage      guidePage;

    @Inject
    public GuidePageController(EventBus eventBus, WorkspaceAgent workspaceAgent, GuidePage guidePage) {
        this.workspaceAgent = workspaceAgent;
        this.guidePage = guidePage;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (event.getProject().getProjectTypeId().equals(Constants.TUTORIAL_ID)) {
                    openTutorialGuide();
                }
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (event.getProject().getDescription().equals(Constants.TUTORIAL_ID)) {
                    closeTutorialGuide();
                }
            }
        });
    }

    /** Open tutorial guide page. */
    public void openTutorialGuide() {
        workspaceAgent.openPart(guidePage, PartStackType.EDITING);
    }

    /** Close tutorial guide page. */
    public void closeTutorialGuide() {
        workspaceAgent.removePart(guidePage);
    }
}
