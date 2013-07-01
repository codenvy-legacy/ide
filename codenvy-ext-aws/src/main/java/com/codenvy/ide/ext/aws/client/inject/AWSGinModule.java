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
package com.codenvy.ide.ext.aws.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientServiceImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.create.CreateApplicationView;
import com.codenvy.ide.ext.aws.client.beanstalk.create.CreateApplicationViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration.EditConfigurationView;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration.EditConfigurationViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.launch.LaunchEnvironmentView;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.launch.LaunchEnvironmentViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentView;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.restart.RestartEnvironmentView;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.restart.RestartEnvironmentViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.terminate.TerminateEnvironmentView;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.terminate.TerminateEnvironmentViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.manage.MainTabPainView;
import com.codenvy.ide.ext.aws.client.beanstalk.manage.MainTabPainViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.manage.ManageApplicationView;
import com.codenvy.ide.ext.aws.client.beanstalk.manage.ManageApplicationViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.update.DescriptionUpdateView;
import com.codenvy.ide.ext.aws.client.beanstalk.update.DescriptionUpdateViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.VersionTabPainView;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.VersionTabPainViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.create.CreateVersionView;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.create.CreateVersionViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.delete.DeleteVersionView;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.delete.DeleteVersionViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy.DeployVersionView;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy.DeployVersionViewImpl;
import com.codenvy.ide.ext.aws.client.beanstalk.wizard.BeanstalkPageView;
import com.codenvy.ide.ext.aws.client.beanstalk.wizard.BeanstalkPageViewImpl;
import com.codenvy.ide.ext.aws.client.ec2.EC2ClientService;
import com.codenvy.ide.ext.aws.client.ec2.EC2ClientServiceImpl;
import com.codenvy.ide.ext.aws.client.ec2.EC2ManagerView;
import com.codenvy.ide.ext.aws.client.ec2.EC2ManagerViewImpl;
import com.codenvy.ide.ext.aws.client.login.LoginView;
import com.codenvy.ide.ext.aws.client.login.LoginViewImpl;
import com.codenvy.ide.ext.aws.client.s3.S3ClientService;
import com.codenvy.ide.ext.aws.client.s3.S3ClientServiceImpl;
import com.codenvy.ide.ext.aws.client.s3.S3ManagerView;
import com.codenvy.ide.ext.aws.client.s3.S3ManagerViewImpl;
import com.codenvy.ide.ext.aws.client.s3.create.S3CreateBucketView;
import com.codenvy.ide.ext.aws.client.s3.create.S3CreateBucketViewImpl;
import com.codenvy.ide.ext.aws.client.s3.upload.S3UploadObjectView;
import com.codenvy.ide.ext.aws.client.s3.upload.S3UploadObjectViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@ExtensionGinModule
public class AWSGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(BeanstalkClientService.class).to(BeanstalkClientServiceImpl.class).in(Singleton.class);
        bind(EC2ClientService.class).to(EC2ClientServiceImpl.class).in(Singleton.class);
        bind(S3ClientService.class).to(S3ClientServiceImpl.class).in(Singleton.class);

        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(EC2ManagerView.class).to(EC2ManagerViewImpl.class).in(Singleton.class);
        bind(S3ManagerView.class).to(S3ManagerViewImpl.class).in(Singleton.class);
        bind(S3CreateBucketView.class).to(S3CreateBucketViewImpl.class).in(Singleton.class);
        bind(S3UploadObjectView.class).to(S3UploadObjectViewImpl.class).in(Singleton.class);

        bind(DescriptionUpdateView.class).to(DescriptionUpdateViewImpl.class).in(Singleton.class);
        bind(BeanstalkPageView.class).to(BeanstalkPageViewImpl.class).in(Singleton.class);
        bind(CreateApplicationView.class).to(CreateApplicationViewImpl.class).in(Singleton.class);
        bind(LaunchEnvironmentView.class).to(LaunchEnvironmentViewImpl.class).in(Singleton.class);
        bind(RebuildEnvironmentView.class).to(RebuildEnvironmentViewImpl.class).in(Singleton.class);
        bind(RestartEnvironmentView.class).to(RestartEnvironmentViewImpl.class).in(Singleton.class);
        bind(TerminateEnvironmentView.class).to(TerminateEnvironmentViewImpl.class).in(Singleton.class);
        bind(EditConfigurationView.class).to(EditConfigurationViewImpl.class).in(Singleton.class);

        bind(CreateVersionView.class).to(CreateVersionViewImpl.class).in(Singleton.class);
        bind(DeleteVersionView.class).to(DeleteVersionViewImpl.class).in(Singleton.class);
        bind(DeployVersionView.class).to(DeployVersionViewImpl.class).in(Singleton.class);

        bind(ManageApplicationView.class).to(ManageApplicationViewImpl.class).in(Singleton.class);
        bind(MainTabPainView.class).to(MainTabPainViewImpl.class).in(Singleton.class);
        bind(VersionTabPainView.class).to(VersionTabPainViewImpl.class).in(Singleton.class);


    }
}
