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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.ext.cloudbees.client.actions.*;
import com.codenvy.ide.ext.cloudbees.client.wizard.CloudBeesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
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
    public CloudBeesExtension(PaaSAgent paasAgent,
                              CloudBeesResources resources,
                              ActionManager actionManager,
                              ShowCloudBeesProjectAction showCloudBeesProjectAction,
                              ShowApplicationsAction showApplicationsAction,
                              CreateApplicationAction createApplicationAction,
                              SwitchAccountAction switchAccountAction,
                              CreateAccountAction createAccountAction,
                              Provider<CloudBeesPagePresenter> wizardPage) {
        resources.cloudBeesCSS().ensureInjected();

        // TODO change hard code types
        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("java", JsonCollections.<String>createArray("Servlet/JSP", "War"));

        JsonArray<Provider<? extends WizardPage>> wizardPages = JsonCollections.createArray();
        wizardPages.add(wizardPage);

        paasAgent.register(ID, ID, resources.cloudBees48(), natures, wizardPages, null);

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