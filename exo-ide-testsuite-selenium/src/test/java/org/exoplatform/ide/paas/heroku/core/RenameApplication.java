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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.AbstractTestModule;
import org.exoplatform.ide.paas.heroku.core.Heroku.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RenameApplication extends AbstractTestModule
{

   public static final String RENAME_VIEW = "//div[@view-id='ideRenameApplicationView']";

   public static final String NEW_NAME_FIELD = "ideRenameApplicationViewNameField";

   public static final String RENAME_BUTTON = "ideRenameApplicationViewRenameButton";

   public static final String CANCEL_BUTTON = "ideRenameApplicationViewCancelButton";

   public void openRenameForm() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU, MenuCommands.PaaS.Heroku.RENAME_APPLICATION);
      waitForElementPresent(RENAME_VIEW);
   }
   
   public void typeNewApplication(String newName)
   {
      selenium().type(NEW_NAME_FIELD, newName);
   }
   
   public void clickReanmeButton() throws Exception
   {
      selenium().click(RENAME_BUTTON);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      if(selenium().isElementPresent(SwitchAccount.LOGIN_FORM))
      {
        IDE().HEROKU.SWITCH_ACCOUNT.loginInHeroku(Messages.LOGIN, Messages.PASSWORD);
      }
   }

}
