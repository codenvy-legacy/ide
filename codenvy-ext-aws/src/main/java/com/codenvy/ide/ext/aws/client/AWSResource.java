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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * AWS client resources.
 *
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

    @Source("Elastic_Beanstalk.png")
    ImageResource elasticBeanstalk();

    @Source("Elastic_Beanstalk_32.png")
    ImageResource elasticBeanstalk32();

    @Source("Elastic_Beanstalk_48.png")
    ImageResource elasticBeanstalk48();

    @Source("switchAccount.png")
    ImageResource switchAccount();

    @Source("edit.png")
    ImageResource edit();

    @Source("delete.png")
    ImageResource delete();

    @Source("general.png")
    ImageResource general();

    @Source("versions.png")
    ImageResource versions();

    @Source("create_app.png")
    ImageResource createApplication();

    @Source("manage_app.png")
    ImageResource manageApplication();

    @Source("new_version.png")
    ImageResource newVersion();

    @Source("environments.png")
    ImageResource environments();

    @Source("new_env.png")
    ImageResource newEnvironment();

    @Source("refresh.png")
    ImageResource s3Refresh();

    @Source("download.png")
    ImageResource download();

    @Source("upload.png")
    ImageResource upload();

    @Source("new-bucket.png")
    ImageResource newBucket();

    @Source("bucket.png")
    ImageResource bucket();

    @Source("s3.png")
    ImageResource s3();

    @Source("ec2.png")
    ImageResource ec2();

    @Source("loader.gif")
    ImageResource loader();

    @Source("ec2_reboot.png")
    ImageResource ec2Reboot();

    @Source("ec2_start.png")
    ImageResource ec2Start();

    @Source("ec2_stop.png")
    ImageResource ec2Stop();

    @Source("ec2_terminate.png")
    ImageResource ec2Terminate();

    @Source("environment_edit.png")
    ImageResource environmentEdit();

    @Source("environment_restart.png")
    ImageResource environmentRestart();

    @Source("environment_rebuild.png")
    ImageResource environmentRebuild();

    @Source("environment_terminate.png")
    ImageResource environmentTerminate();

    @Source("environment_logs.png")
    ImageResource environmentLogs();
}
