/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientServiceImpl;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkControl;
import org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate.TerminateEnvironmentPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.manage.ManageApplicationPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionPresenter;
import org.exoplatform.ide.extension.aws.client.ec2.EC2ClientServiceImpl;
import org.exoplatform.ide.extension.aws.client.ec2.EC2Manager;
import org.exoplatform.ide.extension.aws.client.ec2.stop.StopInstancePresenter;
import org.exoplatform.ide.extension.aws.client.login.LoginPresenter;
import org.exoplatform.ide.extension.aws.client.s3.S3Manager;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 13, 2012 2:57:21 PM anya $
 */
public class AWSExtension extends Extension implements InitializeServicesHandler {

    /** The generator of an {@link AutoBean}. */
    public static final AWSAutoBeanFactory      AUTO_BEAN_FACTORY     = GWT.create(AWSAutoBeanFactory.class);

    /** AWS Localization. */
    public static final AWSLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(AWSLocalizationConstant.class);

    public static final String                  INIT_VER_LABEL        = "initial version";

    public static final String                  ID                    = "AWS";

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(
                                       new PaaS("AWS", "AWS Elastic Beanstalk", new Image(AWSClientBundle.INSTANCE.elasticBeanstalk48()),
                                                new Image(
                                                          AWSClientBundle.INSTANCE.elasticBeanstalk48Disabled()),
                                                Arrays.asList(ProjectType.JAVA, ProjectType.SPRING,
                                                              ProjectType.JSP, ProjectType.WAR),
                                                new DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new BeanstalkControl());

        new CreateApplicationPresenter();
        new ManageApplicationPresenter();
        new LoginPresenter();
        new UpdateApplicationPresenter();
        new CreateVersionPresenter();
        new DeployVersionPresenter();
        new DeleteVersionPresenter();
        new EditConfigurationPresenter();
        new LaunchEnvironmentPresenter();
        new TerminateEnvironmentPresenter();
        new RebuildEnvironmentPresenter();
        new RestartAppServerPresenter();
        new EC2Manager();
        new StopInstancePresenter();
        new S3Manager();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new BeanstalkClientServiceImpl(event.getLoader());
        new EC2ClientServiceImpl(event.getLoader());
    }

    public static boolean canBeDeployedToBeanstalk(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());
        return (targets != null && targets.contains(ID));
    }

    public static boolean isAWSApplication(ProjectModel project) {
        return (project.getProperty("aws-application") != null && project.getProperty("aws-application").getValue() != null);
    }
}
