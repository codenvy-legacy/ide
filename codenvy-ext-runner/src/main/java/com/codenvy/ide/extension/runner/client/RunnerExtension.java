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
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.extension.runner.client.actions.GetLogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.StopAction;
import com.codenvy.ide.extension.runner.client.actions.UpdateAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Runner extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Runner extension.", version = "3.0.0")
public class RunnerExtension {

    @Inject
    public RunnerExtension(RunnerLocalizationConstant localizationConstants, ActionManager actionManager, RunAction runAction,
                           GetLogsAction getLogsAction, StopAction stopAction, UpdateAction updateAction) {
        // register actions
        actionManager.registerAction(localizationConstants.runAppActionId(), runAction);
        actionManager.registerAction(localizationConstants.getAppLogsActionId(), getLogsAction);
        actionManager.registerAction(localizationConstants.stopAppActionId(), stopAction);
        actionManager.registerAction(localizationConstants.updateExtensionActionId(), updateAction);

        // add actions in main menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);
        runMenuActionGroup.add(runAction);
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
        DefaultActionGroup runContextGroup = new DefaultActionGroup(GROUP_RUN_CONTEXT_MENU, false, actionManager);
        actionManager.registerAction(GROUP_RUN_CONTEXT_MENU, runContextGroup);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);
    }
}
