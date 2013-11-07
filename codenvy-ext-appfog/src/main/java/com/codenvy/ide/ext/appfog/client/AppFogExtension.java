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
package com.codenvy.ide.ext.appfog.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.ext.appfog.client.actions.CreateApplicationAction;
import com.codenvy.ide.ext.appfog.client.actions.ShowApplicationsAction;
import com.codenvy.ide.ext.appfog.client.actions.ShowProjectAction;
import com.codenvy.ide.ext.appfog.client.actions.SwitchAccountAction;
import com.codenvy.ide.ext.appfog.client.wizard.AppFogPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
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
    public static final String DEFAULT_SERVER = "https://api.appfog.com";
    public static final String ID             = "AppFog";

    /**
     * Create AppFog extension.
     *
     * @param paasAgent
     * @param resources
     * @param wizardPage
     */
    @Inject
    public AppFogExtension(PaaSAgent paasAgent,
                           AppfogResources resources,
                           ActionManager actionManager,
                           CreateApplicationAction createApplicationAction,
                           ShowApplicationsAction showApplicationsAction,
                           SwitchAccountAction switchAccountAction,
                           ShowProjectAction showProjectAction,
                           Provider<AppFogPagePresenter> wizardPage) {
        resources.appFogCSS().ensureInjected();

        // TODO change hard code types
        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("java", JsonCollections.<String>createArray("Servlet/JSP", "Spring", "War"));
        natures.put("Ruby", JsonCollections.<String>createArray("Rails"));
        natures.put("Python", JsonCollections.<String>createArray());
        natures.put("PHP", JsonCollections.<String>createArray());

        JsonArray<Provider<? extends AbstractPaasPage>> wizardPages = JsonCollections.createArray();
        wizardPages.add(wizardPage);

        paasAgent.register(ID, ID, resources.appfog48(), natures, wizardPages, false);

        actionManager.registerAction("appFogCreateApplication", createApplicationAction);
        actionManager.registerAction("appFogShowApplications", showApplicationsAction);
        actionManager.registerAction("appFogSwitchAccount", switchAccountAction);
        actionManager.registerAction("appFogShowProject", showProjectAction);

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