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
 * Extension add Google App Engine support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "GoogleAppEngine Support.", version = "3.0.0")
public class GAEExtension {
    public static final String ID               = "GAE";
    public static final String APP_ENGINE_SCOPE = "https://www.googleapis.com/auth/appengine.admin";
    public static final String CREATE_APP_URL   = "https://appengine.google.com/start/createapp";

    /** Constructor for Google App Engine extension. */
    @Inject
    public GAEExtension(PaaSAgent paasAgent, GAEResources resources, ActionManager actionManager,
                        LoginAction loginAction, CreateApplicationAction createApplicationAction,
                        Provider<GAEWizardPresenter> wizardPage, UpdateApplicationAction updateApplicationAction,
                        ManageApplicationAction manageApplicationAction) {
        // TODO change hard code types
        JsonArray<String> requiredProjectTypes =
                JsonCollections.createArray("Python", "PHP", "War");

        // TODO
//        paasAgent.registerPaaS(ID, ID, resources.googleAppEngine48(), requiredProjectTypes, wizardPage, null);

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

    /**
     * Checks if user token has Google App Engine token.
     *
     * @param token
     *         authorization token.
     * @return true if Google App Engine scope exist, otherwise false.
     */
    public static boolean isUserHasGaeScopes(Token token) {
        return token != null && token.getScope() != null && token.getScope().contains(APP_ENGINE_SCOPE);
    }

    /**
     * Checks if project has Google App Engine application configuration files.
     *
     * @param project
     *         project to search.
     * @return true if configuration files exist, otherwise false.
     */
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