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
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.extensions.client.actions.GetLogsAction;
import com.codenvy.ide.ext.extensions.client.actions.LaunchAction;
import com.codenvy.ide.ext.extensions.client.actions.StopAction;
import com.codenvy.ide.ext.extensions.client.template.CreateEmptyCodenvyExtensionPage;
import com.codenvy.ide.ext.extensions.client.template.CreateSampleCodenvyExtensionPage;
import com.codenvy.ide.resources.model.Property;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;

/**
 * Entry point for an extension that adds support for running Codenvy-extensions in Codenvy.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Codenvy extensions", version = "3.0.0")
public class ExtRuntimeExtension {
    public static final String CODENVY_EXTENSION_PROJECT_TYPE_ID = "codenvy_extension";
    public static final String EMPTY_EXTENSION_TEMPLATE_ID       = "EmptyCodenvyExtension";
    public static final String GIST_TEMPLATE_ID                  = "gist";

    @Inject
    public ExtRuntimeExtension(TemplateAgent templateAgent,
                               Provider<CreateEmptyCodenvyExtensionPage> createEmptyCodenvyExtensionPage,
                               Provider<CreateSampleCodenvyExtensionPage> createSampleCodenvyExtensionPage,
                               ExtRuntimeLocalizationConstant localizationConstants,
                               ExtRuntimeResources resources,
                               ActionManager actionManager,
                               LaunchAction launchAction,
                               GetLogsAction getLogsAction,
                               StopAction stopAction) {
        // register actions
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);

        actionManager.registerAction(localizationConstants.launchExtensionActionId(), launchAction);
        runMenuActionGroup.add(launchAction);

        actionManager.registerAction(localizationConstants.getExtensionLogsActionId(), getLogsAction);
        runMenuActionGroup.add(getLogsAction);

        actionManager.registerAction(localizationConstants.stopExtensionActionId(), stopAction);
        runMenuActionGroup.add(stopAction);

        templateAgent.register(EMPTY_EXTENSION_TEMPLATE_ID,
                               "Empty Codenvy extension project.",
                               resources.codenvyExtensionTemplate(),
                               CODENVY_EXTENSION_PROJECT_TYPE_ID,
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createEmptyCodenvyExtensionPage));
        templateAgent.register(GIST_TEMPLATE_ID,
                               "Gist extension project.",
                               resources.codenvyExtensionTemplate(),
                               CODENVY_EXTENSION_PROJECT_TYPE_ID,
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createSampleCodenvyExtensionPage));
    }
}
