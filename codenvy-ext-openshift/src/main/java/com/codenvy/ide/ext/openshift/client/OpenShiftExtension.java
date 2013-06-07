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
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.openshift.client.actions.ChangeDomainAction;
import com.codenvy.ide.ext.openshift.client.actions.ShowApplicationsAction;
import com.codenvy.ide.ext.openshift.client.actions.ShowProjectAction;
import com.codenvy.ide.ext.openshift.client.actions.SwitchAccountAction;
import com.codenvy.ide.ext.openshift.client.actions.UpdatePublicKeyAction;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add OpenShift support to the IDE Application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
@Extension(title = "OpenShift Support.", version = "3.0.0")
public class OpenShiftExtension {
    private static final String ID = "OpenShift";

    /**
     * Create OpenShift extension.
     *
     * @param paasAgent
     * @param resources
     * @param wizardPage
     * @param actionManager
     */
    @Inject
    public OpenShiftExtension(PaaSAgent paasAgent, OpenShiftResources resources,
                              Provider<OpenShiftPagePresenter> wizardPage, ActionManager actionManager,
                              ChangeDomainAction changeDomainAction,
                              SwitchAccountAction switchAccountAction,
                              ShowApplicationsAction showApplicationsAction,
                              UpdatePublicKeyAction updatePublicKeyAction,
                              ShowProjectAction showProjectAction
                             ) {
        resources.openShiftCSS().ensureInjected();

        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "nodejs", "War", "Python", "PHP", "Rails");

        paasAgent.registerPaaS(ID, ID, resources.openShift48(), requiredProjectTypes, wizardPage, null);

        actionManager.registerAction("openShiftChangeDomain", changeDomainAction);
        actionManager.registerAction("openShiftSwitchAccount", switchAccountAction);
        actionManager.registerAction("openShiftShowApplications", showApplicationsAction);
        actionManager.registerAction("openShiftUpdatePublicKey", updatePublicKeyAction);
        actionManager.registerAction("opeShoftShowProject", showProjectAction);

        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        DefaultActionGroup openShift = new DefaultActionGroup("OpenShift", true, actionManager);
        actionManager.registerAction("openShiftPaas", openShift);
        paas.add(openShift);

        openShift.add(changeDomainAction);
        openShift.add(switchAccountAction);
        openShift.add(showApplicationsAction);
        openShift.add(updatePublicKeyAction);

        DefaultActionGroup projectPaas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        projectPaas.add(showProjectAction);
    }
}
