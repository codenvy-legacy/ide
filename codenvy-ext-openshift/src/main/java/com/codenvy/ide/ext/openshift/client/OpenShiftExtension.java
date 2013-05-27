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
package com.codenvy.ide.ext.openshift.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.menu.MainMenuAgent;
import com.codenvy.ide.ext.openshift.client.command.*;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPagePresenter;
import com.codenvy.ide.extension.maven.client.template.CreateEmptyProjectPresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
@Extension(title = "OpenShift Support.", version = "3.0.0")
public class OpenShiftExtension {
    private static final String ID = "OpenShift";

    @Inject
    public OpenShiftExtension(PaaSAgent paasAgent, OpenShiftResources resources, MainMenuAgent menu,
                              Provider<OpenShiftPagePresenter> wizardPage, ShowApplicationsCommand showApplicationsCommand,
                              ShowLoginCommand showLoginCommand, ChangeDomainCommand changeDomainCommand,
                              UpdateSshPublicKeyCommand updateSshPublicKeyCommand,
                              ShowOpenShiftProjectCommand showOpenShiftProjectCommand, TemplateAgent templateAgent,
                              CreateEmptyProjectPresenter createProjectProvider) {
        resources.openShiftCSS().ensureInjected();

        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "nodejs", "War", "Python", "PHP", "Rails");

//        templateAgent.registerTemplate("OpenShift foobar", null, JsonCollections.createArray("War"), createProjectProvider, wizardPage);
        paasAgent.registerPaaS(ID, ID, resources.openShift48(), requiredProjectTypes, wizardPage, null);

        menu.addMenuItem("PaaS/OpenShift/Change Domain...", changeDomainCommand);
        menu.addMenuItem("PaaS/OpenShift/Switch Account...", showLoginCommand);
        menu.addMenuItem("PaaS/OpenShift/Applications...", showApplicationsCommand);
        menu.addMenuItem("PaaS/OpenShift/Update Public SSH Key...", updateSshPublicKeyCommand);

        menu.addMenuItem("Project/Paas/OpenShift", showOpenShiftProjectCommand);
    }
}
