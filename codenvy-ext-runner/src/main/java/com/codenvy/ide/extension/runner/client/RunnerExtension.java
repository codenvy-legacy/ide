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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.extension.runner.client.actions.LogsAction;
import com.codenvy.ide.extension.runner.client.actions.RunAction;
import com.codenvy.ide.extension.runner.client.actions.StopAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_PROJECT;
import static com.codenvy.ide.json.JsonCollections.createArray;

/**
 * Maven builder extension entry point.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderExtension.java Feb 21, 2012 1:53:48 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Run App Support.")
public class RunnerExtension {
    public static final String PROJECT_BUILD_GROUP_MAIN_MENU = "ProjectRunGroup";
    /** Channel for the messages containing status of the Maven build job. */
    public static final String RUNNER_RUN_STATUS             = "runner:runStatus:";


    /**
     * Create extension.
     *
     */
    @Inject
    public RunnerExtension(
            RunnerLocalizationConstant localizationConstants,
            ActionManager actionManager,
            RunAction runAction,
            LogsAction logsAction,
            StopAction stopAction) {
        // register actions
        actionManager.registerAction(localizationConstants.runAppControlId(), runAction);
        actionManager.registerAction(localizationConstants.stopAppControlId(), stopAction);
        actionManager.registerAction(localizationConstants.showLogsControlId(), logsAction);


        // compose action group
        DefaultActionGroup buildGroup = new DefaultActionGroup(PROJECT_BUILD_GROUP_MAIN_MENU, false, actionManager);
        buildGroup.add(runAction);
        buildGroup.add(stopAction);
        buildGroup.add(logsAction);

        // add action group to 'Project' menu
        DefaultActionGroup projectMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_PROJECT);
        projectMenuActionGroup.addSeparator();
        projectMenuActionGroup.add(buildGroup);

    }
}