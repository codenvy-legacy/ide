/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.action.IdeActions;
import org.eclipse.che.ide.api.constraints.Anchor;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.extension.builder.client.actions.BuildAction;
import org.eclipse.che.ide.extension.builder.client.console.indicators.ArtifactURLIndicator;
import org.eclipse.che.ide.extension.builder.client.console.indicators.BuildFinishedIndicator;
import org.eclipse.che.ide.extension.builder.client.console.indicators.BuildStartedIndicator;
import org.eclipse.che.ide.extension.builder.client.console.indicators.BuildStatusIndicator;
import org.eclipse.che.ide.extension.builder.client.console.indicators.BuildTimeoutThresholdIndicator;
import org.eclipse.che.ide.extension.builder.client.actions.BrowseTargetFolderAction;
import org.eclipse.che.ide.extension.builder.client.console.BuilderConsolePresenter;
import org.eclipse.che.ide.extension.builder.client.console.BuilderConsoleToolbar;
import org.eclipse.che.ide.extension.builder.client.console.ClearConsoleAction;
import org.eclipse.che.ide.extension.builder.client.console.indicators.BuildTotalTimeIndicator;
import org.eclipse.che.ide.toolbar.ToolbarPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    public BuilderExtension(BuilderResources builderResources) {
        builderResources.builder().ensureInjected();
    }

    @Inject
    private void prepareActions(BuilderLocalizationConstant localizationConstants,
                                ActionManager actionManager,
                                BuildAction buildAction,
                                ClearConsoleAction clearConsoleAction,
                                BrowseTargetFolderAction browseTargetFolderAction) {
        // register actions
        actionManager.registerAction(localizationConstants.buildProjectControlId(), buildAction);
        actionManager.registerAction(localizationConstants.clearConsoleControlId(), clearConsoleAction);
        actionManager.registerAction("browseTargetFolder", browseTargetFolderAction);

        // add actions in main menu
        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_BUILD);
        buildMenuActionGroup.add(buildAction, Constraints.FIRST);
        buildMenuActionGroup.add(clearConsoleAction, Constraints.LAST);
        buildMenuActionGroup.add(browseTargetFolderAction, Constraints.LAST);

        DefaultActionGroup buildToolbarGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_BUILD_TOOLBAR);

        if (buildToolbarGroup == null) {
            buildToolbarGroup = new DefaultActionGroup(IdeActions.GROUP_BUILD_TOOLBAR, false, actionManager);
            DefaultActionGroup rightToolbar = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RIGHT_TOOLBAR);
            rightToolbar.add(buildToolbarGroup, Constraints.FIRST);//new Constraints(Anchor.BEFORE, IdeActions.GROUP_RUN_TOOLBAR));
            actionManager.registerAction(IdeActions.GROUP_BUILD_TOOLBAR, buildToolbarGroup);
        }
        buildToolbarGroup.add(buildAction, Constraints.FIRST);
        buildToolbarGroup.addSeparator();


        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup buildContextGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_BUILD_CONTEXT_MENU);
        buildContextGroup.add(buildAction);
        contextMenuGroup.add(buildContextGroup, new Constraints(Anchor.BEFORE, IdeActions.GROUP_RUN_CONTEXT_MENU));
    }

    @Inject
    private void prepareBuilderConsole(ActionManager actionManager,
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
        workspaceAgent.openPart(builderConsolePresenter, PartStackType.INFORMATION, new Constraints(Anchor.BEFORE, "Runner"));

        // add toolbar with indicators to Builder console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_BUILDER_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(artifactURLIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildStartedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildFinishedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildTimeoutThresholdIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildTotalTimeIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(buildStatusIndicator);
        builderConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}