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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.extension.runner.client.actions.CustomRunAction;
import com.codenvy.ide.extension.runner.client.actions.GetLogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.StopAction;
import com.codenvy.ide.extension.runner.client.actions.UpdateAction;
import com.codenvy.ide.extension.runner.client.actions.ViewRecipeAction;
import com.codenvy.ide.extension.runner.client.console.ClearConsoleAction;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.indicators.ApplicationURLIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerFinishedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerStartedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTimeoutThresholdIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTotalTimeIndicator;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Runner extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Runner", version = "3.0.0")
public class RunnerExtension {
    public static final String GROUP_RUNNER_CONSOLE_TOOLBAR = "RunnerConsoleToolbar";

    @Inject
    public RunnerExtension(RunnerLocalizationConstant localizationConstants,
                           ActionManager actionManager,
                           RunAction runAction,
                           CustomRunAction customRunAction,
                           GetLogsAction getLogsAction,
                           StopAction stopAction,
                           UpdateAction updateAction,
                           ClearConsoleAction clearConsoleAction,
                           ViewRecipeAction viewRecipeAction,
                           ApplicationURLIndicator applicationURLIndicator,
                           RunnerStartedIndicator runnerStartedIndicator,
                           RunnerTimeoutThresholdIndicator runnerTimeoutThresholdIndicator,
                           RunnerFinishedIndicator runnerFinishedIndicator,
                           RunnerTotalTimeIndicator runnerTotalTimeIndicator,
                           WorkspaceAgent workspaceAgent,
                           RunnerConsolePresenter runnerConsolePresenter,
                           RunnerResources runnerResources,
                           @RunnerConsoleToolbar ToolbarPresenter runnerConsoleToolbar) {
        runnerResources.runner().ensureInjected();

        // register actions
        actionManager.registerAction(localizationConstants.runAppActionId(), runAction);
        actionManager.registerAction(localizationConstants.customRunAppActionId(), customRunAction);
        actionManager.registerAction(localizationConstants.getAppLogsActionId(), getLogsAction);
        actionManager.registerAction(localizationConstants.stopAppActionId(), stopAction);
        actionManager.registerAction(localizationConstants.updateExtensionActionId(), updateAction);
        actionManager.registerAction(localizationConstants.viewRecipeActionId(), viewRecipeAction);

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction);
        runMenuActionGroup.add(customRunAction, new Constraints(Anchor.AFTER, localizationConstants.runAppActionId()));
        runMenuActionGroup.add(getLogsAction);
        runMenuActionGroup.add(stopAction);
        runMenuActionGroup.add(clearConsoleAction);
        runMenuActionGroup.add(updateAction);
        runMenuActionGroup.add(viewRecipeAction);

        // add actions on main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        runToolbarGroup.add(runAction);
        mainToolbarGroup.add(runToolbarGroup);

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);

        // add Runner console
        workspaceAgent.openPart(runnerConsolePresenter, PartStackType.INFORMATION);

        // add toolbar with actions to Builder console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_RUNNER_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(stopAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(viewRecipeAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(applicationURLIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerStartedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTimeoutThresholdIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerFinishedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTotalTimeIndicator);
        runnerConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}
