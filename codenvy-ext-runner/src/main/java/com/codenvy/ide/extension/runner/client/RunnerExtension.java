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
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;

/**
 * Runner extension entry point.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnerExtension.java Jul 2, 2013 4:14:56 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Running project support.", version = "3.0.0")
public class RunnerExtension {

    @Inject
    public RunnerExtension(RunnerLocalizationConstant localizationConstants,
                           ActionManager actionManager,
                           RunAction runAction,
                           GetLogsAction getLogsAction,
                           StopAction stopAction) {
        // register actions
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);

        actionManager.registerAction(localizationConstants.runAppActionId(), runAction);
        runMenuActionGroup.add(runAction);

        actionManager.registerAction(localizationConstants.getAppLogsActionId(), getLogsAction);
        runMenuActionGroup.add(getLogsAction);

        actionManager.registerAction(localizationConstants.stopAppActionId(), stopAction);
        runMenuActionGroup.add(stopAction);
    }
}
