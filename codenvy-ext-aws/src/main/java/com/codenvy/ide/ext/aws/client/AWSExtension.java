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
package com.codenvy.ide.ext.aws.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.ext.aws.client.beanstalk.wizard.BeanstalkPagePresenter;
import com.codenvy.ide.ext.aws.client.command.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
@Extension(title = "AWS Support.", version = "3.0.0")
public class AWSExtension {
    private static final String ID             = "AWS";
    public static final  String INIT_VER_LABEL = "initial version";

    /**
     * Create CloudFoundry extension.
     *
     * @param paasAgent
     * @param resource
     * @param actionManager
     * @param switchAccountAction
     * @param ec2ManagementAction
     * @param s3ManagementAction
     * @param beanstalkManagementAction
     * @param createApplicationManagementAction
     *
     * @param wizardPage
     */
    @Inject
    public AWSExtension(PaaSAgent paasAgent, AWSResource resource, ActionManager actionManager, SwitchAccountAction switchAccountAction,
                        EC2ManagementAction ec2ManagementAction, S3ManagementAction s3ManagementAction,
                        BeanstalkManagementAction beanstalkManagementAction,
                        CreateApplicationManagementAction createApplicationManagementAction,
                        Provider<BeanstalkPagePresenter> wizardPage) {
        // TODO change hard code types
        JsonStringMap<JsonArray<String>> natures = JsonCollections.createStringMap();
        natures.put("java", JsonCollections.<String>createArray("Servlet/JSP", "Spring", "War"));

        JsonArray<Provider<? extends WizardPage>> wizardPages = JsonCollections.createArray();
        wizardPages.add(wizardPage);

        paasAgent.register(ID, "Amazon Web Services", resource.elasticBeanstalk48(), natures, wizardPages, false);

        actionManager.registerAction("awsSwitchAccount", switchAccountAction);
        actionManager.registerAction("awsEc2Management", ec2ManagementAction);
        actionManager.registerAction("awsS3Management", s3ManagementAction);
        actionManager.registerAction("awsBeanstalkManagement", beanstalkManagementAction);
        actionManager.registerAction("awsCreateApplication", createApplicationManagementAction);

        DefaultActionGroup paas = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PAAS);
        DefaultActionGroup aws = new DefaultActionGroup("AWS", true, actionManager);
        actionManager.registerAction("AWS", aws);
        paas.add(aws);

        aws.add(switchAccountAction);
        aws.add(ec2ManagementAction);
        aws.add(s3ManagementAction);
        aws.add(beanstalkManagementAction);
        aws.add(createApplicationManagementAction);

        DefaultActionGroup projectPaaS = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_PROJECT_PAAS);
        projectPaaS.add(beanstalkManagementAction);
    }
}
