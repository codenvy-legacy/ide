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
