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

package org.exoplatform.ide.paas.cloudfoundry.core;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Applications extends AbstractTestModule
{

   public interface Locators
   {

      String VIEW_LOCATOR = "//div[@view-id='ideCloudFoundryApplicationsView']";

      String APPLICATIONS_TABLE = "//table[@id='applicationsListGrid']";

      String SERVER_FIELD = "//input[@name='applicationsTarget']";

      String SHOW_BUTTON = "applicationsShowButton";

      String CLOSE_BUTTON = "applicationsCloseButton";

   }

   public void openFromMenu() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.CloudFoundry.CLOUDFOUNDRY,
         MenuCommands.PaaS.CloudFoundry.APPPLICATIONS);
   }

   public void waitForOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
   }

   public void waitForClosed() throws Exception
   {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
   }

   public void clickCloseButton()
   {
      selenium().click(Locators.CLOSE_BUTTON);
   }

   public void clickShowButton()
   {
      selenium().click(Locators.SHOW_BUTTON);
   }

   public int getApplicationsCount()
   {
      int amount = 0;
      for (int i = 1; i < 1000; i++)
      {
         String locator = Locators.APPLICATIONS_TABLE + "/tbody[1]/tr[" + i + "]";
         if (!selenium().isElementPresent(locator))
         {
            break;
         }

         amount++;
      }

      return amount;
   }
   
   /**
    * Get name of application
    * 
    * @param row row
    * @return name of applicaton
    */
   public String getApplicationName(int row) {
      String locator = Locators.APPLICATIONS_TABLE + "/tbody[1]/tr[" + row + "]/td[1]";
      return selenium().getText(locator);
   }
   
   public String getAppplicationInstances(int row) {
      String locator = Locators.APPLICATIONS_TABLE + "/tbody[1]/tr[" + row + "]/td[2]";
      return selenium().getText(locator);
   }
   
   public String getApplicationState(int row) {
      String locator = Locators.APPLICATIONS_TABLE + "/tbody[1]/tr[" + row + "]/td[3]";
      return selenium().getText(locator);
   }
   
   public String getApplicationURL(int row) {
      String locator = Locators.APPLICATIONS_TABLE + "/tbody[1]/tr[" + row + "]/td[4]";
      return selenium().getText(locator);
   }
   
}
