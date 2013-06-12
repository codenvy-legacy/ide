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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.cloudbees.client.actions.CreateAccountAction;
import com.codenvy.ide.ext.cloudbees.client.actions.CreateApplicationAction;
import com.codenvy.ide.ext.cloudbees.client.actions.ShowApplicationsAction;
import com.codenvy.ide.ext.cloudbees.client.actions.ShowCloudBeesProjectAction;
import com.codenvy.ide.ext.cloudbees.client.actions.SwitchAccountAction;
import com.codenvy.ide.ext.cloudbees.client.wizard.CloudBeesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add CloudBees support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "CloudBees Support.", version = "3.0.0")
public class CloudBeesExtension {
    private static final String ID = "CloudBees";

    @Inject
    public CloudBeesExtension(PaaSAgent paasAgent, CloudBeesResources resources,
                              ActionManager actionManager,
                              ShowCloudBeesProjectAction showCloudBeesProjectAction,
                              ShowApplicationsAction showApplicationsAction,
                              CreateApplicationAction createApplicationAction,
                              SwitchAccountAction switchAccountAction,
                              CreateAccountAction createAccountAction,
                              Provider<CloudBeesPagePresenter> wizardPage) {
        resources.cloudBeesCSS().ensureInjected();

        // TODO change hard code types
        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "War");
        paasAgent.registerPaaS(ID, ID, resources.cloudBees48(), requiredProjectTypes, wizardPage, null);

        actionManager.registerAction("cloudBeesShowProject", showCloudBeesProjectAction);
        actionManager.registerAction("showCloudBeesApplications", showApplicationsAction);
        actionManager.registerAction("createCloudBeesApplication", createApplicationAction);
        actionManager.registerAction("switchCloudBeesAccount", switchAccountAction);
        actionManager.registerAction("createCloudBeesAccount", createAccountAction);

        DefaultActionGroup projectPaas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        projectPaas.add(showCloudBeesProjectAction);

        DefaultActionGroup cloudBees = new DefaultActionGroup("CloudBees", true, actionManager);
        actionManager.registerAction("cloudBeesPaas", cloudBees);
        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        paas.add(cloudBees);

        cloudBees.add(createApplicationAction);
        cloudBees.add(showApplicationsAction);
        cloudBees.add(switchAccountAction);
        cloudBees.add(createAccountAction);
    }
}