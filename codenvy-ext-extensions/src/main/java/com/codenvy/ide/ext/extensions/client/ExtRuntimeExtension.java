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
package com.codenvy.ide.ext.extensions.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.extensions.client.actions.GetLogsAction;
import com.codenvy.ide.ext.extensions.client.actions.LaunchAction;
import com.codenvy.ide.ext.extensions.client.actions.StopAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;

/**
 * Entry point for an extension that adds support for running Codenvy extensions.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Codenvy extensions", version = "3.0.0")
public class ExtRuntimeExtension {

    @Inject
    public ExtRuntimeExtension(ExtRuntimeLocalizationConstant localizationConstants,
                               ActionManager actionManager,
                               LaunchAction launchAction,
                               GetLogsAction getLogsAction,
                               StopAction stopAction,
                               IconRegistry iconRegistry) {
        Map<String, String> icons = new HashMap<>();
        icons.put("codenvy_extension.projecttype.big.icon", "codenvy-ext/codenvy.jpg");
        icons.put("war.projecttype.small.icon", "java-extension/web_app_big.png");
        icons.put("war.folder.small.icon", "java-extension/package.gif");
        icons.put("war.file.small.icon", "java-extension/java-class.png");
        icons.put("java.class", "java-extension/java-class.png");
        icons.put("java.package", "java-extension/package.gif");
        // register actions
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);

        actionManager.registerAction(localizationConstants.launchExtensionActionId(), launchAction);
        runMenuActionGroup.add(launchAction);

        actionManager.registerAction(localizationConstants.getExtensionLogsActionId(), getLogsAction);
        runMenuActionGroup.add(getLogsAction);

        actionManager.registerAction(localizationConstants.stopExtensionActionId(), stopAction);
        runMenuActionGroup.add(stopAction);
    }
}
