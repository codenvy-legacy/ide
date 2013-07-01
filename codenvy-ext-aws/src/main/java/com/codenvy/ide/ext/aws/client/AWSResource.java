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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AWSResource extends ClientBundle {
    public interface AWSCSS extends CssResource {
        String login();

        String loginFont();

        String loginErrorFont();

        String project();

        String labelH();

        String link();

        String textinput();

        String appInfo();

        String event();
    }

    @Source({"AWS.css", "com/codenvy/ide/api/ui/style.css"})
    AWSCSS awsCss();

    AWSResource INSTANCE = GWT.<AWSResource>create(AWSResource.class);

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk.png")
    ImageResource elasticBeanstalk();

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk_Disabled.png")
    ImageResource elasticBeanstalkDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk_32.png")
    ImageResource elasticBeanstalk32();

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk_32_Disabled.png")
    ImageResource elasticBeanstalk32Disabled();

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk_48.png")
    ImageResource elasticBeanstalk48();

    @Source("com/codenvy/ide/ext/aws/client/images/Elastic_Beanstalk_48_Disabled.png")
    ImageResource elasticBeanstalk48Disabled();

    @Source("com/codenvy/ide/ext/aws/client/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/aws/client/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/edit.png")
    ImageResource edit();

    @Source("com/codenvy/ide/ext/aws/client/images/edit_Disabled.png")
    ImageResource editDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/delete.png")
    ImageResource delete();

    @Source("com/codenvy/ide/ext/aws/client/images/delete_Disabled.png")
    ImageResource deleteDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/general.png")
    ImageResource general();

    @Source("com/codenvy/ide/ext/aws/client/images/versions.png")
    ImageResource versions();

    @Source("com/codenvy/ide/ext/aws/client/images/create_app.png")
    ImageResource createApplication();

    @Source("com/codenvy/ide/ext/aws/client/images/create_app_Disabled.png")
    ImageResource createApplicationDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/manage_app.png")
    ImageResource manageApplication();

    @Source("com/codenvy/ide/ext/aws/client/images/manage_app_Disabled.png")
    ImageResource manageApplicationDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/new_version.png")
    ImageResource newVersion();

    @Source("com/codenvy/ide/ext/aws/client/images/new_version_Disabled.png")
    ImageResource newVersionDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environments.png")
    ImageResource environments();

    @Source("com/codenvy/ide/ext/aws/client/images/new_env.png")
    ImageResource newEnvironment();

    @Source("com/codenvy/ide/ext/aws/client/images/new_env_Disabled.png")
    ImageResource newEnvironmentDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/refresh.png")
    ImageResource s3Refresh();

    @Source("com/codenvy/ide/ext/aws/client/images/refresh_Disabled.png")
    ImageResource s3RefreshDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/download.png")
    ImageResource download();

    @Source("com/codenvy/ide/ext/aws/client/images/download_Disabled.png")
    ImageResource downloadDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/upload.png")
    ImageResource upload();

    @Source("com/codenvy/ide/ext/aws/client/images/upload_Disabled.png")
    ImageResource uploadDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/new-bucket.png")
    ImageResource newBucket();

    @Source("com/codenvy/ide/ext/aws/client/images/bucket.png")
    ImageResource bucket();

    @Source("com/codenvy/ide/ext/aws/client/images/s3.png")
    ImageResource s3();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2.png")
    ImageResource ec2();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_Disabled.png")
    ImageResource ec2Disabled();

    @Source("com/codenvy/ide/ext/aws/client/images/loader.gif")
    ImageResource loader();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_reboot.png")
    ImageResource ec2Reboot();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_reboot_Disabled.png")
    ImageResource ec2RebootDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_start.png")
    ImageResource ec2Start();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_start_Disabled.png")
    ImageResource ec2StartDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_stop.png")
    ImageResource ec2Stop();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_stop_Disabled.png")
    ImageResource ec2StopDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_terminate.png")
    ImageResource ec2Terminate();

    @Source("com/codenvy/ide/ext/aws/client/images/ec2_terminate_Disabled.png")
    ImageResource ec2TerminateDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_edit.png")
    ImageResource environmentEdit();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_edit_Disabled.png")
    ImageResource environmentEditDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_restart.png")
    ImageResource environmentRestart();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_restart_Disabled.png")
    ImageResource environmentRestartDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_rebuild.png")
    ImageResource environmentRebuild();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_rebuild_Disabled.png")
    ImageResource environmentRebuildDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_terminate.png")
    ImageResource environmentTerminate();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_terminate_Disabled.png")
    ImageResource environmentTerminateDisabled();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_logs.png")
    ImageResource environmentLogs();

    @Source("com/codenvy/ide/ext/aws/client/images/environment_logs_Disabled.png")
    ImageResource environmentLogsDisabled();
}
