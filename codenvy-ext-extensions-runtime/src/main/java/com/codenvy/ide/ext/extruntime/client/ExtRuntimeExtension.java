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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.extruntime.client.actions.GetLogsAction;
import com.codenvy.ide.ext.extruntime.client.actions.LaunchAction;
import com.codenvy.ide.ext.extruntime.client.actions.StopAction;
import com.codenvy.ide.ext.extruntime.client.template.CreateSampleCodenvyExtensionProjectPresenter;
import com.codenvy.ide.ext.extruntime.client.template.CreateEmptyCodenvyExtensionProjectPresenter;
import com.codenvy.ide.ext.extruntime.client.wizard.ExtensionPagePresenter;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;
import static com.codenvy.ide.json.JsonCollections.createArray;

/**
 * Entry point for an extension that adds support for running Codenvy-extensions in Codenvy.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeExtension.java Jul 2, 2013 4:14:56 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Codenvy extensions runtime support.", version = "3.0.0")
public class ExtRuntimeExtension {
    public static final String CODENVY_EXTENSION_PROJECT_TYPE = "CodenvyExtension";

    @Inject
    public ExtRuntimeExtension(TemplateAgent templateAgent,
                               CreateEmptyCodenvyExtensionProjectPresenter createEmptyCodenvyExtensionProjectPresenter,
                               CreateSampleCodenvyExtensionProjectPresenter createSampleCodenvyExtensionProjectPresenter,
                               Provider<ExtensionPagePresenter> wizardPage,
                               ProjectTypeAgent projectTypeAgent,
                               ExtRuntimeLocalizationConstant localizationConstants,
                               ExtRuntimeResources resources,
                               ActionManager actionManager,
                               LaunchAction launchAction,
                               GetLogsAction getLogsAction,
                               StopAction stopAction) {
        // register actions
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup) actionManager.getAction(GROUP_RUN_MAIN_MENU);

        actionManager.registerAction(localizationConstants.launchExtensionActionlId(), launchAction);
        runMenuActionGroup.add(launchAction);

        actionManager.registerAction(localizationConstants.getExtensionLogsActionId(), getLogsAction);
        runMenuActionGroup.add(getLogsAction);

        actionManager.registerAction(localizationConstants.stopExtensionActionId(), stopAction);
        runMenuActionGroup.add(stopAction);

        // register project type
        projectTypeAgent.registerProjectType(CODENVY_EXTENSION_PROJECT_TYPE, "Codenvy extension",
                resources.codenvyExtensionProject());

        // register templates
        templateAgent.registerTemplate("Empty Codenvy extension project.",
                resources.codenvyExtensionTemplate(),
                createArray(CODENVY_EXTENSION_PROJECT_TYPE),
                createEmptyCodenvyExtensionProjectPresenter, null);

        templateAgent.registerTemplate("Sample Codenvy extension project. Illustrates simple example that uses Codenvy API.",
                resources.codenvyExtensionTemplate(),
                createArray(CODENVY_EXTENSION_PROJECT_TYPE),
                createSampleCodenvyExtensionProjectPresenter, wizardPage);
    }
}
