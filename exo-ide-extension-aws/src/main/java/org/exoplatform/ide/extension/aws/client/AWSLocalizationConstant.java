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

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 9:51:41 AM anya $
 * 
 */
public interface AWSLocalizationConstant extends com.google.gwt.i18n.client.Messages
{
   @Key("button.create")
   String createButton();

   @Key("button.cancel")
   String cancelButton();

   @Key("button.ok")
   String okButton();

   @Key("button.rename")
   String renameButton();

   @Key("button.delete")
   String deleteButton();

   @Key("button.close")
   String closeButton();

   @Key("button.add")
   String addButton();
   
   @Key("button.next")
   String nextButton();
   
   @Key("button.back")
   String backButton();
   
   @Key("button.finish")
   String finishButton();

   //Controls
   @Key("control.beanstalk.id")
   String beanstalkControlId();

   @Key("control.beanstalk.title")
   String beanstalkControlTitle();

   @Key("control.beanstalk.prompt")
   String beanstalkControlPrompt();

   @Key("control.createApp.id")
   String createApplicationControlId();

   @Key("control.createApp.title")
   String createApplicationControlTitle();

   @Key("control.createApp.prompt")
   String createApplicationControlPrompt();

   @Key("control.deleteApp.id")
   String deleteApplicationControlId();

   @Key("control.deleteApp.title")
   String deleteApplicationControlTitle();

   @Key("control.deleteApp.prompt")
   String deleteApplicationControlPrompt();

   @Key("control.login.id")
   String loginControlId();

   @Key("control.login.title")
   String loginControlTitle();

   @Key("control.login.prompt")
   String loginControlPrompt();

   // Login View
   @Key("login.title")
   String loginTitle();

   @Key("login.field.access.key")
   String loginAccessKeyField();

   @Key("login.field.secret.key")
   String loginSecretKeyField();

   @Key("login.error.invalidKeyValue")
   String loginErrorInvalidKeyValue();

   @Key("login.success")
   String loginSuccess();

   // Create Application
   @Key("create.application.view.title")
   String createApplicationViewTitle();

   @Key("create.application.name.field")
   String createApplicationNameField();

   @Key("create.application.description.field")
   String createApplicationDescriptionField();

   @Key("create.application.s3.bucket.field")
   String createApplicationS3BucketField();

   @Key("create.application.s3.key.field")
   String createApplicationS3KeyField();

   @Key("create.application.s3.title")
   String createApplicationS3Title();
}
