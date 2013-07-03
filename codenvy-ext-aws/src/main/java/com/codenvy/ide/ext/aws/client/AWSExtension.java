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
package com.codenvy.ide.ext.aws.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.aws.client.beanstalk.wizard.BeanstalkPagePresenter;
import com.codenvy.ide.ext.aws.client.command.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
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

    @Inject
    public AWSExtension(PaaSAgent paasAgent, AWSResource resource, ActionManager actionManager, SwitchAccountAction switchAccountAction,
                        EC2ManagementAction ec2ManagementAction, S3ManagementAction s3ManagementAction,
                        BeanstalkManagementAction beanstalkManagementAction,
                        CreateApplicationManagementAction createApplicationManagementAction,
                        Provider<BeanstalkPagePresenter> wizardPage) {
        JsonArray<String> requiredProjectTypes = JsonCollections.createArray("Servlet/JSP", "Spring", "War");
        paasAgent.registerPaaS(ID, "Amazon Web Services", resource.elasticBeanstalk48(), requiredProjectTypes, wizardPage, null);

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
