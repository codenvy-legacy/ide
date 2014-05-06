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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.extension.runner.client.console.ClearConsoleAction;
import com.codenvy.ide.extension.runner.client.actions.CustomRunAction;
import com.codenvy.ide.extension.runner.client.actions.GetLogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.StopAction;
import com.codenvy.ide.extension.runner.client.actions.UpdateAction;
import com.codenvy.ide.extension.runner.client.console.ApplicationURLAction;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
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
                           ApplicationURLAction applicationURLAction,
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

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction);
        runMenuActionGroup.add(customRunAction, new Constraints(Anchor.AFTER, localizationConstants.runAppActionId()));
        runMenuActionGroup.add(getLogsAction);
        runMenuActionGroup.add(stopAction);
        runMenuActionGroup.add(updateAction);

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
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(applicationURLAction);
        runnerConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}
