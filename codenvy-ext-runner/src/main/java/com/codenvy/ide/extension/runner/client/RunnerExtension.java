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

import com.codenvy.api.project.shared.Constants;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.extension.runner.client.actions.CustomRunAction;
import com.codenvy.ide.extension.runner.client.actions.EditCustomEnvironmentsAction;
import com.codenvy.ide.extension.runner.client.actions.GetLogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.RunWithGroup;
import com.codenvy.ide.extension.runner.client.actions.ShutdownAction;
import com.codenvy.ide.extension.runner.client.actions.ViewRecipeAction;
import com.codenvy.ide.extension.runner.client.console.ClearConsoleAction;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.indicators.ApplicationURLIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerFinishedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerStartedIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTimeoutThresholdIndicator;
import com.codenvy.ide.extension.runner.client.console.indicators.RunnerTotalTimeIndicator;
import com.codenvy.ide.extension.runner.client.wizard.SelectRunnerPagePresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Runner extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Runner", version = "3.0.0")
public class RunnerExtension {
    public static final String GROUP_RUNNER_CONSOLE_TOOLBAR  = "RunnerConsoleToolbar";
    public static final String GROUP_RUN_WITH                = "RunWithGroup";
    /** Key for user preference which contains default RAM size. */
    public final static String PREFS_RUNNER_RAM_SIZE_DEFAULT = "runner.ram-size.default";

    @Inject
    public RunnerExtension(RunnerResources runnerResources,
                           ProjectTypeWizardRegistry wizardRegistry,
                           Provider<SelectRunnerPagePresenter> runnerPagePresenter) {
        runnerResources.runner().ensureInjected();

        // TODO: temporary solution to add runner page for Blank project
        ProjectWizard wizard = wizardRegistry.getWizard(Constants.BLANK_ID);
        wizard.addPage(runnerPagePresenter);
        wizardRegistry.addWizard(Constants.BLANK_ID, wizard);
    }

    @Inject
    private void prepareActions(RunnerLocalizationConstant localizationConstants,
                                RunnerResources resources,
                                ActionManager actionManager,
                                RunAction runAction,
                                CustomRunAction customRunAction,
                                RunWithGroup runWithGroup,
                                EditCustomEnvironmentsAction editCustomEnvironmentsAction,
                                GetLogsAction getLogsAction,
                                ShutdownAction shutdownAction,
                                ClearConsoleAction clearConsoleAction,
                                ViewRecipeAction viewRecipeAction) {
        // register actions
        actionManager.registerAction(localizationConstants.runAppActionId(), runAction);
        actionManager.registerAction(localizationConstants.customRunAppActionId(), customRunAction);
        actionManager.registerAction(localizationConstants.editCustomEnvironmentsActionId(), editCustomEnvironmentsAction);
        actionManager.registerAction(localizationConstants.getAppLogsActionId(), getLogsAction);
        actionManager.registerAction(localizationConstants.shutdownActionId(), shutdownAction);
        actionManager.registerAction(localizationConstants.viewRecipeActionId(), viewRecipeAction);
        actionManager.registerAction(GROUP_RUN_WITH, runWithGroup);

        // prepare 'Run With...' group
        runWithGroup.add(editCustomEnvironmentsAction);
        runWithGroup.addSeparator();

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction);
        runMenuActionGroup.add(customRunAction, new Constraints(Anchor.AFTER, localizationConstants.runAppActionId()));
        runMenuActionGroup.add(getLogsAction);
        runMenuActionGroup.add(shutdownAction);
        runMenuActionGroup.add(clearConsoleAction);
        runMenuActionGroup.addSeparator();
        runMenuActionGroup.add(viewRecipeAction);
        runMenuActionGroup.add(runWithGroup, new Constraints(Anchor.AFTER, localizationConstants.viewRecipeActionId()));

        // add actions on main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        runToolbarGroup.add(runAction);
        runToolbarGroup.add(runWithGroup, Constraints.LAST);
        mainToolbarGroup.add(runToolbarGroup);
    }

    @Inject
    private void prepareRunnerConsole(ActionManager actionManager,
                                      ShutdownAction shutdownAction,
                                      ClearConsoleAction clearConsoleAction,
                                      ViewRecipeAction viewRecipeAction,
                                      ApplicationURLIndicator applicationURLIndicator,
                                      RunnerStartedIndicator runnerStartedIndicator,
                                      RunnerTimeoutThresholdIndicator runnerTimeoutThresholdIndicator,
                                      RunnerFinishedIndicator runnerFinishedIndicator,
                                      RunnerTotalTimeIndicator runnerTotalTimeIndicator,
                                      WorkspaceAgent workspaceAgent,
                                      RunnerConsolePresenter runnerConsolePresenter,
                                      @RunnerConsoleToolbar ToolbarPresenter runnerConsoleToolbar) {
        workspaceAgent.openPart(runnerConsolePresenter, PartStackType.INFORMATION, new Constraints(Anchor.AFTER, "Builder"));

        // add toolbar with actions on Runner console
        DefaultActionGroup consoleToolbarActionGroup = new DefaultActionGroup(GROUP_RUNNER_CONSOLE_TOOLBAR, false, actionManager);
        consoleToolbarActionGroup.add(shutdownAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(clearConsoleAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(viewRecipeAction);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(applicationURLIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerStartedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerFinishedIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTimeoutThresholdIndicator);
        consoleToolbarActionGroup.addSeparator();
        consoleToolbarActionGroup.add(runnerTotalTimeIndicator);
        runnerConsoleToolbar.bindMainGroup(consoleToolbarActionGroup);
    }
}
