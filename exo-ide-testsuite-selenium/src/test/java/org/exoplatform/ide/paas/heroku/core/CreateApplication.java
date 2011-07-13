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

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CreateApplication extends AbstractTestModule
{

   private static final String CREATE_APP_FORM = "//div[@view-id='ideCreateApplicationView']";

   public static final String WORK_DIR_FIELD = "ideCreateApplicationViewWorkDirField";

   public static final String APP_NAME_FIELD = "ideCreateApplicationViewNameField";

   public static final String REMOTE_REPO_FIELD = "ideCreateApplicationViewRemoteNameField";

   public static final String CREATE_BUTTON = "ideCreateApplicationViewCreateButton";

   public void openCreateApplicationForm() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU,
         MenuCommands.PaaS.Heroku.CREATE_APPLICATION);
      waitForElementPresent(CREATE_APP_FORM);
   }
   
   public void typeAppName(String name)
   {
      selenium().type(APP_NAME_FIELD, name);
   }
   
   public void createApp()
   {
      selenium().click(CREATE_BUTTON);
   }

}
