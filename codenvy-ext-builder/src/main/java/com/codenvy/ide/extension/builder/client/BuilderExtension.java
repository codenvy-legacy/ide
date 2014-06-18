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
package com.codenvy.ide.extension.builder.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.extension.builder.client.actions.BuildAction;
import com.codenvy.ide.extension.builder.client.console.indicators.ArtifactURLIndicator;
import com.codenvy.ide.extension.builder.client.console.indicators.BuildFinishedIndicator;
import com.codenvy.ide.extension.builder.client.console.indicators.BuildStartedIndicator;
import com.codenvy.ide.extension.builder.client.console.indicators.BuildStatusIndicator;
import com.codenvy.ide.extension.builder.client.console.indicators.BuildTimeoutThresholdIndicator;
import com.codenvy.ide.extension.builder.client.console.indicators.BuildTotalTimeIndicator;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleToolbar;
import com.codenvy.ide.extension.builder.client.console.ClearConsoleAction;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Builder extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Builder", version = "3.0.0")
public class BuilderExtension {
    /** WebSocket channel to get build task status. */
    public static final String BUILD_STATUS_CHANNEL          = "builder:status:";
    /** WebSocket channel to get builder output. */
    public static final String BUILD_OUTPUT_CHANNEL          = "builder:output:";
    public static final String GROUP_BUILDER_CONSOLE_TOOLBAR = "BuilderConsoleToolbar";

    @Inject
    public BuilderExtension(BuilderLocalizationConstant localizationConstants,
                            BuilderResources builderResources,
                            ActionManager actionManager,
                            BuildAction buildAction,
                            ClearConsoleAction clearConsoleAction,
                            ArtifactURLIndicator artifactURLIndicator,
                            BuildStartedIndicator buildStartedIndicator,
                            BuildFinishedIndicator buildFinishedIndicator,
                            BuildTotalTimeIndicator buildTotalTimeIndicator,
                            BuildTimeoutThresholdIndicator buildTimeoutThresholdIndicator,
                            BuildStatusIndicator buildStatusIndicator,
                            WorkspaceAgent workspaceAgent,
                            BuilderConsolePresenter builderConsolePresenter,
                            @BuilderConsoleToolbar ToolbarPresenter builderConsoleToolbar) {
        builderResources.builder().ensureInjected();

        actionManager.registerAction(localizationConstants.buildProjectControlId(), buildAction);
        actionManager.registerAction(localizationConstants.clearConsoleControlId(), clearConsoleAction);

        // add actions in main menu
        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);
        buildMenuActionGroup.add(buildAction);

        // add actions on main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup buildToolbarGroup = new DefaultActionGroup(GROUP_BUILD_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_BUILD_TOOLBAR, buildToolbarGroup);
        buildToolbarGroup.add(buildAction);
        buildToolbarGroup.addSeparator();
        mainToolbarGroup.add(buildToolbarGroup, new Constraints(Anchor.BEFORE, GROUP_RUN_TOOLBAR));

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup buildContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD_CONTEXT_MENU);
        buildContextGroup.add(buildAction);
        contextMenuGroup.add(buildContextGroup, new Constraints(Anchor.BEFORE, GROUP_RUN_CONTEXT_MENU));

        // add Builder console
        workspaceAgent.openPart(builderConsolePresenter, PartStackType.INFORMATION);

        // add toolbar with indicators to Builder console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_BUILDER_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(artifactURLIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildStartedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildTimeoutThresholdIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildFinishedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildTotalTimeIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildStatusIndicator);
        builderConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}