/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 10:34:27 AM anya $
 * 
 */
public interface AWSClientBundle extends ClientBundle
{
   AWSClientBundle INSTANCE = GWT.<AWSClientBundle> create(AWSClientBundle.class);

   @Source("org/exoplatform/ide/extension/aws/client/images/Elastic_Beanstalk.png")
   ImageResource elasticBeanstalk();

   @Source("org/exoplatform/ide/extension/aws/client/images/Elastic_Beanstalk_Disabled.png")
   ImageResource elasticBeanstalkDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/switchAccount.png")
   ImageResource switchAccount();

   @Source("org/exoplatform/ide/extension/aws/client/images/switchAccount_Disabled.png")
   ImageResource switchAccountDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/edit.png")
   ImageResource edit();

   @Source("org/exoplatform/ide/extension/aws/client/images/edit_Disabled.png")
   ImageResource editDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/delete.png")
   ImageResource delete();

   @Source("org/exoplatform/ide/extension/aws/client/images/delete_Disabled.png")
   ImageResource deleteDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/general.png")
   ImageResource general();

   @Source("org/exoplatform/ide/extension/aws/client/images/versions.png")
   ImageResource versions();

   @Source("org/exoplatform/ide/extension/aws/client/images/create_app.png")
   ImageResource createApplication();

   @Source("org/exoplatform/ide/extension/aws/client/images/create_app_Disabled.png")
   ImageResource createApplicationDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/manage_app.png")
   ImageResource manageApplication();

   @Source("org/exoplatform/ide/extension/aws/client/images/manage_app_Disabled.png")
   ImageResource manageApplicationDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/new_version.png")
   ImageResource newVersion();

   @Source("org/exoplatform/ide/extension/aws/client/images/new_version_Disabled.png")
   ImageResource newVersionDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/environments.png")
   ImageResource environments();

   @Source("org/exoplatform/ide/extension/aws/client/images/new_env.png")
   ImageResource newEnvironment();

   @Source("org/exoplatform/ide/extension/aws/client/images/new_env_Disabled.png")
   ImageResource newEnvironmentDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/refresh.png")
   ImageResource s3Refresh();

   @Source("org/exoplatform/ide/extension/aws/client/images/refresh_Disabled.png")
   ImageResource s3RefreshDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/download.png")
   ImageResource download();

   @Source("org/exoplatform/ide/extension/aws/client/images/download_Disabled.png")
   ImageResource downloadDisabled();

   @Source("org/exoplatform/ide/extension/aws/client/images/upload.png")
   ImageResource upload();

   @Source("org/exoplatform/ide/extension/aws/client/images/upload_Disabled.png")
   ImageResource uploadDisabled();
   
   @Source("org/exoplatform/ide/extension/aws/client/images/new-bucket.png")
   ImageResource newBucket();
   
   @Source("org/exoplatform/ide/extension/aws/client/images/bucket.png")
   ImageResource bucket();
   
   @Source("org/exoplatform/ide/extension/aws/client/images/s3.png")
   ImageResource s3();
   
   @Source("org/exoplatform/ide/extension/aws/client/images/loader.gif")
   ImageResource loader();
}
