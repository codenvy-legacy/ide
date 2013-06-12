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
package com.codenvy.ide.ext.appfog.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.appfog.client.actions.CreateApplicationAction;
import com.codenvy.ide.ext.appfog.client.actions.ShowApplicationsAction;
import com.codenvy.ide.ext.appfog.client.actions.ShowProjectAction;
import com.codenvy.ide.ext.appfog.client.actions.SwitchAccountAction;
import com.codenvy.ide.ext.appfog.client.wizard.AppFogPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add AppFog support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "AppFog Support.", version = "3.0.0")
public class AppFogExtension {
    public static final  String DEFAULT_SERVER = "https://api.appfog.com";
    private static final String ID             = "AppFog";

    /**
     * Create AppFog extension.
     *
     * @param paasAgent
     * @param resources
     * @param wizardPage
     */
    @Inject
    public AppFogExtension(PaaSAgent paasAgent, AppfogResources resources,
                           ActionManager actionManager,
                           CreateApplicationAction createApplicationAction,
                           ShowApplicationsAction showApplicationsAction,
                           SwitchAccountAction switchAccountAction,
                           ShowProjectAction showProjectAction,
                           Provider<AppFogPagePresenter> wizardPage) {
        resources.appFogCSS().ensureInjected();

        // TODO change hard code types
        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "Rails", "Spring", "War", "Python", "PHP");
        paasAgent.registerPaaS(ID, ID, resources.appfog48(), requiredProjectTypes, wizardPage, null);

        actionManager.registerAction("appFogCreateApplication", createApplicationAction);
        actionManager.registerAction("appFogShowApplications", showApplicationsAction);
        actionManager.registerAction("appFogSwitchAccount", switchAccountAction);
        actionManager.registerAction("appFogShowProject", showApplicationsAction);

        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        DefaultActionGroup appFog = new DefaultActionGroup("AppFog", true, actionManager);
        actionManager.registerAction("AppFog", appFog);
        paas.add(appFog);

        appFog.add(createApplicationAction);
        appFog.add(showApplicationsAction);
        appFog.add(switchAccountAction);

        DefaultActionGroup projectPaas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        projectPaas.add(showProjectAction);
    }
}