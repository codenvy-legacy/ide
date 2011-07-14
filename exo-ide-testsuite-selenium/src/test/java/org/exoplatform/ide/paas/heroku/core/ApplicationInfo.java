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

import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ApplicationInfo extends AbstractTestModule
{

   public static final String APP_INFO_FORM = "//div[@view-id='ideApplicationInfoView']";

   public static final String APP_INFO_GRID_ID = "ideApplicationInfoGrid";

   private static final int NUMBER_OF_ROW = 12;

   /**
    * Open Heroku application information form
    * @throws Exception
    */
   public void openApplicationInfoForm() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.Heroku.HEROKU,
         MenuCommands.PaaS.Heroku.APPLICATION_INFO);
      waitForElementPresent(APP_INFO_FORM);
   }

   /**
    * Get row locator for application property in grid
    * @param name of property
    * @return locator
    */
   private String getRowLocator(String name)
   {
      String locator = null;

      for (int rowNumber = 1; rowNumber <= NUMBER_OF_ROW; rowNumber++)
      {
         locator = "//table[@id='" + APP_INFO_GRID_ID + "']/tbody/tr[" + rowNumber + "]/td";
         if (selenium().isElementPresent(locator))
         {
            String text = selenium().getText(locator);
            if (text.equals(name))
            {
               return locator;
            }
         }
         else
         {
            return null;
         }
      }
      return null;
   }

   /**
    * Get value of Application property
    * @param propertyName
    * @return property value
    */
   private String getAppPropery(String propertyName)
   {
      String rowLocator = getRowLocator(propertyName);

      if (!selenium().isElementPresent(rowLocator))
      {
         fail("Can't find row locator: " + rowLocator);
      }
      String bindLocator = rowLocator + "[2]";

      return selenium().getText(bindLocator);

   }

   /**
    * Get Name property
    * @return name of application
    */
   public String getName()
   {
      return getAppPropery("Name");
   }

   /**
    * Get Application WEB URL
    * @return WEB URL of application
    */
   public String getWebUrl()
   {
      return getAppPropery("WebUrl");
   }
   
   /**
    * Get application Git URL
    * @return git url
    */
   public String getGitUrl()
   {
      return getAppPropery("GitUrl");
   }
   
   /**
    * Get Application Owner
    * @return owner of application
    */
   public String getAppOwner()
   {
      return getAppPropery("Owner");
   }
   
}
