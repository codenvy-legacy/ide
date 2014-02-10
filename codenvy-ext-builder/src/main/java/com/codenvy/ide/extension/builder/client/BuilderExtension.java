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
import com.codenvy.ide.extension.builder.client.actions.BuildAction;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_PROJECT;

/**
 * Builder extension entry point.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderExtension.java Feb 21, 2012 1:53:48 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Building project", version = "3.0.0")
public class BuilderExtension {
    public static final String PROJECT_BUILD_GROUP_MAIN_MENU   = "ProjectBuildGroup";
    /** Channel for the messages containing status of the Maven build job. */
    public static final String BUILD_STATUS_CHANNEL            = "builder:status:";


    /**
     * Create extension.
     *
     */
    @Inject
    public BuilderExtension(BuilderLocalizationConstant localizationConstants,
                            ActionManager actionManager,
                            BuildAction buildAction) {
        // register actions
        actionManager.registerAction(localizationConstants.buildProjectControlId(), buildAction);
//        actionManager.registerAction(localizationConstants.buildAndPublishProjectControlId(), buildAndPublishAction);

        // compose action group
        DefaultActionGroup buildGroup = new DefaultActionGroup(PROJECT_BUILD_GROUP_MAIN_MENU, false, actionManager);
        buildGroup.add(buildAction);
//        buildGroup.add(buildAndPublishAction);

        // add action group to 'Project' menu
        DefaultActionGroup projectMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_PROJECT);
        projectMenuActionGroup.addSeparator();
        projectMenuActionGroup.add(buildGroup);

    }
}