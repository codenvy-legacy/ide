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
package org.exoplatform.ide.paas.heroku.core;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.core.AbstractTestModule;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class Heroku extends AbstractTestModule
{
   public interface Messages
   {
      String LOGED_IN = "[INFO] Logged in Heroku successfully.";

      String DELETED = "[INFO] Application is successfully deleted on Heroku.";

      String KEYS_DEPLOYED = "[INFO] Public keys are successfully deployed on Heroku.";

      String LOGIN = "test@test.com";

      String PASSWORD = "test";
   }

   public final SwitchAccount SWITCH_ACCOUNT = new SwitchAccount();

   public final CreateApplication CREATE_APP = new CreateApplication();

   public final DeleteApplication DELETE_APP = new DeleteApplication();

   public final RenameApplication RENAME_APP = new RenameApplication();

   public final ApplicationInfo APP_INFO = new ApplicationInfo();

   public final Rake RAKE = new Rake();

   public static final int logout() throws Exception
   {
      HttpURLConnection connection = null;
      int status = -1;
      try
      {
         URL url = new URL("http", BaseTest.IDE_HOST, BaseTest.IDE_PORT, BaseTest.REST_CONTEXT + "/ide/heroku/logout");
         connection = Utils.getConnection(url);
         connection.setRequestMethod("POST");
         connection.getResponseCode();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
      return status;
   }

   public void deployPublicKey() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU,
         MenuCommands.PaaS.Heroku.DEPLOY_PUBLIC_KEY);
   }

}
