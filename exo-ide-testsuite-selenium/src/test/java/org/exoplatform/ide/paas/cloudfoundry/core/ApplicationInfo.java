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

public class ApplicationInfo extends AbstractTestModule
{

   public interface Locators
   {

      String VIEW_LOCATOR = "//div[@view-id='ideCloudFoundryApplicationInfoView']";

      String NAME = "appNameLabel";

      String STATE = "appStateLabel";

      String INSTANCES = "appInstancesLabel";

      String VERSION = "appVersionLabel";

      String DISK = "appDiskLabel";

      String MEMORY = "appMemoryLabel";

      String MODEL = "appModelLabel";

      String STACK = "appStackLabel";

      String URIS_TABLE = "ideCloudFoundryAppUrisGridView";

      String OK_BUTTON_ID = "applicationInfoOkButton";

   }

   public void openFromMenu() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.CloudFoundry.CLOUDFOUNDRY,
         MenuCommands.PaaS.CloudFoundry.APPLICATION_INFO);
   }

   public void waitForApplicationInfoOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      Thread.sleep(500);
   }

   public void waitForApplicationInfoClosed() throws Exception
   {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
      Thread.sleep(500);
   }

   public String getName()
   {
      return selenium().getText(Locators.NAME);
   }

   public String getState()
   {
      return selenium().getText(Locators.STATE);
   }

   public String getInstances()
   {
      return selenium().getText(Locators.INSTANCES);
   }

   public String getVersion()
   {
      return selenium().getText(Locators.VERSION);
   }

   public String getDisk()
   {
      return selenium().getText(Locators.DISK);
   }

   public String getMemory()
   {
      return selenium().getText(Locators.MEMORY);
   }

   public String getModel()
   {
      return selenium().getText(Locators.MODEL);
   }

   public String getStack()
   {
      return selenium().getText(Locators.STACK);
   }

   public String getURIs()
   {
      return selenium().getText(Locators.URIS_TABLE);
   }

   public void clickOkButton()
   {
      selenium().click(Locators.OK_BUTTON_ID);
   }

}
