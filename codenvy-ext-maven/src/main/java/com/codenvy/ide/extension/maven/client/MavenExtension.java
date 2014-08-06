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
package com.codenvy.ide.extension.maven.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.maven.client.actions.CustomBuildAction;
import com.codenvy.ide.extension.maven.client.wizard.MavenPagePresenter;
import com.codenvy.ide.extension.runner.client.wizard.SelectRunnerPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD;

/**
 * Builder extension entry point.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Building Maven project", version = "3.0.0")
public class MavenExtension {
    /** Create extension. */
    @Inject
    public MavenExtension(MavenLocalizationConstant localizationConstants,
                          BuilderLocalizationConstant builderLocalizationConstant,
                          ActionManager actionManager,
                          CustomBuildAction customBuildAction,
                          Provider<MavenPagePresenter> mavenPagePresenter,
                          Provider<SelectRunnerPagePresenter> runnerPagePresenter,
                          ProjectTypeWizardRegistry wizardRegistry,
                          MavenResources resources,
                          IconRegistry iconRegistry,
                          NotificationManager notificationManager) {
        actionManager.registerAction(localizationConstants.buildProjectControlId(), customBuildAction);

        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);
        buildMenuActionGroup.add(customBuildAction, new Constraints(Anchor.AFTER, builderLocalizationConstant.buildProjectControlId()));

        ProjectWizard wizard = new ProjectWizard(notificationManager);
        wizard.addPage(mavenPagePresenter);
        wizard.addPage(runnerPagePresenter);
        wizardRegistry.addWizard(Constants.MAVEN_ID, wizard);
        // register new Icons for maven projecttype
        iconRegistry.registerIcon(new Icon("maven.projecttype.big.icon", resources.mavenJarBigIcon()));
        iconRegistry.registerIcon(new Icon("maven.folder.small.icon", resources.packageIcon()));
        iconRegistry.registerIcon(new Icon("maven/java.file.small.icon", resources.javaFile()));
        iconRegistry.registerIcon(new Icon("maven/xml.file.small.icon", resources.xmlFile()));
        iconRegistry.registerIcon(new Icon("maven/css.file.small.icon", resources.cssFile()));
        iconRegistry.registerIcon(new Icon("maven/js.file.small.icon", resources.jsFile()));
        iconRegistry.registerIcon(new Icon("maven/json.file.small.icon", resources.jsonFile()));
        iconRegistry.registerIcon(new Icon("maven/html.file.small.icon", resources.htmlFile()));
        iconRegistry.registerIcon(new Icon("maven/jsp.file.small.icon", resources.jspFile()));
        iconRegistry.registerIcon(new Icon("maven/gif.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/jpg.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/png.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/pom.xml.file.small.icon", resources.maven()));

    }
}