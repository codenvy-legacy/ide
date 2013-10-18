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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.extension.cloudfoundry.client.action.CreateApplicationAction;
import com.codenvy.ide.extension.cloudfoundry.client.action.ShowApplicationsAction;
import com.codenvy.ide.extension.cloudfoundry.client.action.ShowCloudFoundryProjectAction;
import com.codenvy.ide.extension.cloudfoundry.client.action.ShowLoginAction;
import com.codenvy.ide.extension.cloudfoundry.client.wizard.CloudFoundryPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add Cloud Foundry support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Cloud Foundry Support.", version = "3.0.0")
public class CloudFoundryExtension {
    /** Defined CloudFoundry PaaS providers. */
    public static enum PAAS_PROVIDER {
        CLOUD_FOUNDRY("cloudfoundry"),
        WEB_FABRIC("tier3webfabric");

        /** Project's type name. */
        private String label;

        /**
         * @param label
         *         provider's label
         */
        private PAAS_PROVIDER(String label) {
            this.label = label;
        }

        /** @return {@link String} provider's label */
        public String value() {
            return label;
        }
    }

    /** Default CloudFoundry server. */
    public static final String DEFAULT_CF_SERVER = "http://api.cloudfoundry.com";
    public static final String CF_ID             = "CloudFoundry";
    public static final String WF_ID             = "Tier3WF";
    public static final String ID                = "CloudFoundry";

    /**
     * Create CloudFoundry extension.
     *
     * @param paasAgent
     * @param resources
     * @param wizardPage
     */
    @Inject
    public CloudFoundryExtension(PaaSAgent paasAgent, CloudFoundryResources resources, ActionManager actionManager,
                                 ShowCloudFoundryProjectAction showCloudFoundryProjectAction,
                                 CreateApplicationAction createApplicationAction,
                                 ShowApplicationsAction showApplicationsAction,
                                 ShowLoginAction showLoginAction,
                                 Provider<CloudFoundryPagePresenter> wizardPage) {

        resources.cloudFoundryCss().ensureInjected();

        // TODO change hard code types
        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("java", JsonCollections.<String>createArray("Servlet/JSP", "Spring", "War"));
        natures.put("Ruby", JsonCollections.<String>createArray("Rails"));

        JsonArray<Provider<? extends WizardPage>> wizardPages = JsonCollections.createArray();
        wizardPages.add(wizardPage);

        paasAgent.register(ID, ID, resources.cloudFoundry48(), natures, wizardPages, false);

        DefaultActionGroup projectPaas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        actionManager.registerAction("showCloudfoundryProject", showCloudFoundryProjectAction);
        projectPaas.add(showCloudFoundryProjectAction);
        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        DefaultActionGroup cloudfoundry = new DefaultActionGroup("CloudFoudry", true, actionManager);
        actionManager.registerAction("cloudfoundryGroup", cloudfoundry);
        paas.add(cloudfoundry);


        actionManager.registerAction("createCloudfoundry", createApplicationAction);
        actionManager.registerAction("showCloudfoundryApplications", showApplicationsAction);
        actionManager.registerAction("showCloudfoundryLogin", showLoginAction);

        cloudfoundry.add(createApplicationAction);
        cloudfoundry.add(showApplicationsAction);
        cloudfoundry.add(showLoginAction);
    }
}