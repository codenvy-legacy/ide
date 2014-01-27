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
import com.codenvy.ide.ext.extensions.client.template.sample.CreateSampleCodenvyExtensionPage;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.resources.model.Property;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;

/**
 * Entry point for an extension that adds support for running Codenvy-extensions in Codenvy.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeExtension.java Jul 2, 2013 4:14:56 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Codenvy extensions", version = "3.0.0")
public class ExtRuntimeExtension {
    public static final String CODENVY_EXTENSION_PROJECT_TYPE = "CodenvyExtension";
    public static final String EMPTY_EXTENSION_ID             = "EmptyCodenvyExtension";
    public static final String SAMPLE_EXTENSION_ID            = "SampleCodenvyExtension";

    @Inject
    public ExtRuntimeExtension(TemplateAgent templateAgent,
                               Provider<CreateEmptyCodenvyExtensionPage> createEmptyCodenvyExtensionPage,
                               Provider<CreateSampleCodenvyExtensionPage> createSampleCodenvyExtensionPage,
                               ProjectTypeAgent projectTypeAgent,
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

        Array<Property> codenvyExtensionProperties = Collections.createArray();
        codenvyExtensionProperties.add(new Property("nature.mixin", Collections.createArray("CodenvyExtension")));
        codenvyExtensionProperties.add(new Property("exoide:projectDescription", Collections.createArray("Codenvy extension.")));
        codenvyExtensionProperties.add(new Property("runner.name", Collections.createArray("sdk")));
        codenvyExtensionProperties.add(new Property("vfs:projectType", Collections.createArray("CodenvyExtension")));
        codenvyExtensionProperties.add(new Property("nature.primary", Collections.createArray("java")));
        codenvyExtensionProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        codenvyExtensionProperties.add(new Property("builder.maven.targets", Collections.createArray("clean", "install")));
        codenvyExtensionProperties.add(new Property("builder.name", Collections.createArray("maven")));
        codenvyExtensionProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/main/resources")));

        // register project type
//        projectTypeAgent.register(CODENVY_EXTENSION_PROJECT_TYPE,
//                                  "Codenvy extension",
//                                  resources.codenvyExtensionProject(),
//                                  PRIMARY_NATURE,
//                                  Collections.createArray(CODENVY_EXTENSION_PROJECT_TYPE),
//                                  codenvyExtensionProperties);

        // register templates
        templateAgent.register(EMPTY_EXTENSION_ID,
                               "Empty Codenvy extension project.",
                               resources.codenvyExtensionTemplate(),
                               PRIMARY_NATURE,
//                               Collections.createArray(CODENVY_EXTENSION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       createEmptyCodenvyExtensionPage));

        templateAgent.register(SAMPLE_EXTENSION_ID,
                               "Gist extension project.",
                               resources.codenvyExtensionTemplate(),
                               PRIMARY_NATURE,
//                               Collections.createArray(CODENVY_EXTENSION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       createSampleCodenvyExtensionPage));
    }
}
