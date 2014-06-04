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
package com.codenvy.ide.tutorial.notification;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.notification.action.ShowErrorNotification;
import com.codenvy.ide.tutorial.notification.action.ShowInfoNotification;
import com.codenvy.ide.tutorial.notification.action.ShowProgressNotification;
import com.codenvy.ide.tutorial.notification.action.ShowWarningNotification;
import com.codenvy.ide.tutorial.notification.part.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_MENU;
import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;

/** Extension used to demonstrate the Notification feature. */
@Singleton
@Extension(title = "Notification tutorial", version = "1.0.0")
public class NotificationTutorialExtension {
    public static final String NOTIFICATION_TUTORIAL_GROUP = "Notification";

    @Inject
    public NotificationTutorialExtension(ActionManager actionManager, ShowInfoNotification showInfoNotification,
                                         ShowWarningNotification showWarningNotification, ShowErrorNotification showErrorNotification,
                                         ShowProgressNotification showProgressNotification, WorkspaceAgent workspaceAgent,
                                         TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, EDITING);

        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup notificationGroup = new DefaultActionGroup(NOTIFICATION_TUTORIAL_GROUP, false, actionManager);
        actionManager.registerAction(NOTIFICATION_TUTORIAL_GROUP, notificationGroup);
        mainMenu.add(notificationGroup);

        notificationGroup.add(showInfoNotification);
        notificationGroup.add(showWarningNotification);
        notificationGroup.add(showErrorNotification);
        notificationGroup.addSeparator();
        notificationGroup.add(showProgressNotification);
    }
}