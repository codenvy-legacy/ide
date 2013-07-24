/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.extruntime.client.actions.LaunchAction;
import com.codenvy.ide.ext.extruntime.client.actions.StopAction;
import com.codenvy.ide.ext.extruntime.client.template.CreateCodenvyExtensionProjectPresenter;
import com.codenvy.ide.ext.extruntime.client.wizard.ExtensionPagePresenter;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_RUN_MAIN_MENU;
import static com.codenvy.ide.json.JsonCollections.createArray;

/**
 * Entry point for an extension that adds support for running Codenvy-extensions from Codenvy.
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
                               CreateCodenvyExtensionProjectPresenter createCodenvyExtensionProjectPresenter,
                               Provider<ExtensionPagePresenter> wizardPage,
                               ProjectTypeAgent projectTypeAgent,
                               ExtRuntimeLocalizationConstant localizationConstants,
                               ExtRuntimeResources resources,
                               ActionManager actionManager,
                               LaunchAction launchAction,
                               StopAction stopAction) {
        // register actions
        actionManager.registerAction(localizationConstants.launchExtensionActionlId(), launchAction);
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_MAIN_MENU);
        runMenuActionGroup.add(launchAction);

        actionManager.registerAction(localizationConstants.stopExtensionActionlId(), stopAction);
        runMenuActionGroup.add(stopAction);

        // register template
        templateAgent.registerTemplate("Codenvy extension project. Illustrates simple example that uses Codenvy SDK.",
                                       resources.codenvyExtensionProject(),
                                       createArray(CODENVY_EXTENSION_PROJECT_TYPE),
                                       createCodenvyExtensionProjectPresenter, wizardPage);

        // register project type
        projectTypeAgent.registerProjectType(CODENVY_EXTENSION_PROJECT_TYPE, "Codenvy extension",
                                             resources.newCodenvyExtensionProject());
    }
}
