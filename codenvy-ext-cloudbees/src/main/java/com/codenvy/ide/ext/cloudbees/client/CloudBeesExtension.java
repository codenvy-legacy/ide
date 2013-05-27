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
import com.codenvy.ide.api.ui.menu.MainMenuAgent;
import com.codenvy.ide.ext.cloudbees.client.command.*;
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
    public CloudBeesExtension(PaaSAgent paasAgent, CloudBeesResources resources, MainMenuAgent menu,
                              ShowLoginCommand loginCommand, ShowCreateApplicationCommand createApplicationCommand,
                              ShowApplicationsCommand applicationsCommand, ShowCreateAccountCommand createAccountCommand,
                              ShowCloudBeesProjectCommand showCloudBeesProjectCommand, Provider<CloudBeesPagePresenter> wizardPage) {
        resources.cloudBeesCSS().ensureInjected();

        // TODO change hard code types
        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "War");
        paasAgent.registerPaaS(ID, ID, resources.cloudBees48(), requiredProjectTypes, wizardPage, null);

        menu.addMenuItem("PaaS/CloudBees/Create Application...", createApplicationCommand);
        menu.addMenuItem("PaaS/CloudBees/Applications...", applicationsCommand);
        menu.addMenuItem("PaaS/CloudBees/Switch Account...", loginCommand);
        menu.addMenuItem("PaaS/CloudBees/Create Account...", createAccountCommand);
        menu.addMenuItem("Project/Paas/CloudBes", showCloudBeesProjectCommand);
    }
}