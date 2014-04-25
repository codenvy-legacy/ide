/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.extension.builder.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.extension.builder.client.actions.BuildAction;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;

/**
 * Builder extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Builder", version = "3.0.0")
public class BuilderExtension {
    public static final String BUILD_STATUS_CHANNEL = "builder:status:";
    public static final String BUILD_OUTPUT_CHANNEL = "builder:output:";

    /** Create extension. */
    @Inject
    public BuilderExtension(BuilderLocalizationConstant localizationConstants,
                            ActionManager actionManager,
                            BuildAction buildAction,
                            WorkspaceAgent workspaceAgent,
                            BuilderConsolePresenter builderConsolePresenter) {
        actionManager.registerAction(localizationConstants.buildProjectControlId(), buildAction);

        // add actions in main menu
        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);
        buildMenuActionGroup.add(buildAction);

        // add actions on main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup buildToolbarGroup = new DefaultActionGroup(GROUP_BUILD_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_BUILD_TOOLBAR, buildToolbarGroup);
        buildToolbarGroup.add(buildAction);
        buildToolbarGroup.addSeparator();
        mainToolbarGroup.add(buildToolbarGroup);

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup buildContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD_CONTEXT_MENU);
        buildContextGroup.add(buildAction);
        contextMenuGroup.add(buildContextGroup);

        // add Builder console
        workspaceAgent.openPart(builderConsolePresenter, PartStackType.INFORMATION);
    }
}