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
package com.codenvy.ide.ext.gae.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.gae.client.actions.CreateApplicationAction;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.actions.ManageApplicationAction;
import com.codenvy.ide.ext.gae.client.actions.UpdateApplicationAction;
import com.codenvy.ide.ext.gae.client.wizard.GAEWizardPresenter;
import com.codenvy.ide.ext.gae.shared.Token;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add GoogleAppEngine support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "GoogleAppEngine Support.", version = "3.0.0")
public class GAEExtension {
    public static final String ID               = "GAE";
    public static final String APP_ENGINE_SCOPE = "https://www.googleapis.com/auth/appengine.admin";
    public static final String CREATE_APP_URL   = "https://appengine.google.com/start/createapp";

    @Inject
    public GAEExtension(PaaSAgent paasAgent, GAEResources resources, ActionManager actionManager,
                        LoginAction loginAction, CreateApplicationAction createApplicationAction,
                        Provider<GAEWizardPresenter> wizardPage, UpdateApplicationAction updateApplicationAction,
                        ManageApplicationAction manageApplicationAction) {
        // TODO change hard code types
        JsonArray<String> requiredProjectTypes =
                JsonCollections.createArray("Python", "PHP", "War");

        paasAgent.registerPaaS(ID, ID, resources.googleAppEngine48(), requiredProjectTypes, wizardPage, null);

        actionManager.registerAction("gaeLoginAction", loginAction);
        actionManager.registerAction("gaeCreateAppAction", createApplicationAction);
        actionManager.registerAction("gaeUpdateAppAction", updateApplicationAction);

        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        DefaultActionGroup gaeActionGroup = new DefaultActionGroup("Google App Engine", true, actionManager);
        actionManager.registerAction("Google App Engine", gaeActionGroup);

        paas.add(gaeActionGroup);

        gaeActionGroup.add(loginAction);
        gaeActionGroup.add(createApplicationAction);
        gaeActionGroup.add(updateApplicationAction);

        DefaultActionGroup projectPaaS = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        projectPaaS.add(manageApplicationAction);
    }

    public static boolean isUserHasGaeScopes(Token token) {
        return token != null && token.getScope() != null && token.getScope().contains(APP_ENGINE_SCOPE);
    }

    public static boolean isAppEngineProject(Project project) {
        if (project == null) {
            return false;
        }

        final String projectType = (String)project.getPropertyValue("vfs:projectType");

        if (projectType.equals(JavaExtension.JAVA_WEB_APPLICATION_PROJECT_TYPE)) {
            return project.findResourceByName("appengine-web.xml", File.TYPE) != null;
        } else {
            return project.findResourceByName("app.yaml", File.TYPE) != null;
        }
    }
}