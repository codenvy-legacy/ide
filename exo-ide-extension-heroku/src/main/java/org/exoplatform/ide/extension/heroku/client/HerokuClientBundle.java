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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Heroku client resources (images).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 10:45:11 AM anya $
 * 
 */
public interface HerokuClientBundle extends ClientBundle
{
   HerokuClientBundle INSTANCE = GWT.<HerokuClientBundle> create(HerokuClientBundle.class);

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/ok_Disabled.png")
   ImageResource okButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/help.png")
   ImageResource helpButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/help_Disabled.png")
   ImageResource helpButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/run.png")
   ImageResource runButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/run_Disabled.png")
   ImageResource runButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/properties.png")
   ImageResource propertiesButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/properties_Disabled.png")
   ImageResource propertiesButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/edit.png")
   ImageResource editButton();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/edit_Disabled.png")
   ImageResource editButtonDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku.png")
   ImageResource heroku();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku_Disabled.png")
   ImageResource herokuDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/addKeys.png")
   ImageResource addKeys();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/addKeys_Disabled.png")
   ImageResource addKeysDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/clearKeys.png")
   ImageResource clearKeys();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/clearKeys_Disabled.png")
   ImageResource clearKeysDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/changeStack.png")
   ImageResource changeStack();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/changeStack_Disabled.png")
   ImageResource changeStackDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/createApp.png")
   ImageResource createApplication();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/createApp_Disabled.png")
   ImageResource createApplicationDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/destroyApp.png")
   ImageResource destroyApplication();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/destroyApp_Disabled.png")
   ImageResource destroyApplicationDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/rake.png")
   ImageResource rake();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/rake_Disabled.png")
   ImageResource rakeDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/renameApp.png")
   ImageResource renameApplication();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/renameApp_Disabled.png")
   ImageResource renameApplicationDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/appInfo.png")
   ImageResource applicationInfo();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/appInfo_Disabled.png")
   ImageResource applicationInfoDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount.png")
   ImageResource switchAccount();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount_Disabled.png")
   ImageResource switchAccountDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/getLogs.png")
   ImageResource getLogs();

   @Source("org/exoplatform/ide/extension/heroku/images/buttons/getLogs_Disabled.png")
   ImageResource getLogsDisabled();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/logs.png")
   ImageResource logs();

   @Source("org/exoplatform/ide/extension/heroku/images/controls/logs_Disabled.png")
   ImageResource logsDisabled();
}
