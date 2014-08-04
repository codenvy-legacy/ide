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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.ext.tutorials.client.action.ShowTutorialGuideAction;
import com.codenvy.ide.ext.tutorials.client.wizard.ExtensionPagePresenter;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_WINDOW;

/**
 * Entry point for an extension that adds support to work with tutorial projects.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Codenvy tutorial projects", version = "3.0.0")
public class TutorialsExtension {
    /** Default name of the file that contains tutorial description. */
    public static final String DEFAULT_README_FILE_NAME = "guide.html";

    @Inject
    public TutorialsExtension(TutorialsResources resources,
                              TutorialsLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              ShowTutorialGuideAction showAction,
                              ProjectTypeWizardRegistry wizardRegistry,
                              NotificationManager notificationManager,
                              IconRegistry iconRegistry,
                              Provider<ExtensionPagePresenter> extensionPagePresenter) {
        resources.tutorialsCss().ensureInjected();
        // register new Icons for samples and codenvy projecttypes
        iconRegistry.registerIcon(new Icon("samples.projecttype.icon", resources.projecttypeSamples()));
        iconRegistry.registerIcon(new Icon("samples-helloworld.projecttype.icon", resources.projecttypeSamples()));
        iconRegistry.registerIcon(new Icon("codenvy.projecttype.icon", resources.projecttypeCodenvy()));
        Map<String, String> icons = new HashMap<>(1);
        icons.put("codenvy_tutorial.projecttype.big.icon", "codenvy-tutorial/codenvy.jpg");

        // register actions
        DefaultActionGroup windowMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_WINDOW);

        actionManager.registerAction(localizationConstants.showTutorialGuideActionId(), showAction);
        windowMenuActionGroup.add(showAction);
        ProjectWizard wizard = new ProjectWizard(notificationManager);
        wizard.addPage(extensionPagePresenter);
        wizardRegistry.addWizard(Constants.TUTORIAL_ID, wizard);
        wizardRegistry.addWizard(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID, wizard);
    }
}
