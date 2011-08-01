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
package org.exoplatform.ide.paas.openshift.core;

import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.AbstractTestModule;
import org.exoplatform.ide.utils.AbstractTextUtil;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateApplication extends AbstractTestModule
{

   public static interface Locators
   {
      String CREATE_APPLICATION_WINDOW = "ideCreateApplicationView-window";

      String APPLICATION_WORKING_DIRECTORY_FIELD = "ideCreateApplicationViewWorkDirField";

      String APPLICATION_NAME_FIELD = "ideCreateApplicationViewNameField";

      String APPLICATION_TYPE_FIELD = "ideCreateApplicationViewTypeField";

      String CREATE_BUTTON = "ideCreateApplicationViewCreateButton";

      String CANCEL_BUTTON = "ideCreateApplicationViewCancelButton";

   }

   public void openCreateApplicationWindow() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.OpenShift.OPENSHIFT,
         MenuCommands.PaaS.OpenShift.CREATE_APPLICATION);
      waitForElementPresent(Locators.CREATE_APPLICATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public boolean isCreateApplicationWindowOpened()
   {
      return selenium().isElementPresent(Locators.CREATE_APPLICATION_WINDOW);
   }

   public void waitForCreateApplicationWindowPresent() throws Exception
   {
      waitForElementPresent(Locators.CREATE_APPLICATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void waitForCreateApplicationWindowNotPresent() throws Exception
   {
      waitForElementNotPresent(Locators.CREATE_APPLICATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public String getApplicationWorkingDirectory()
   {
      return selenium().getText(Locators.APPLICATION_WORKING_DIRECTORY_FIELD);
   }

   public void typeApplicationName(String applicationName) throws Exception
   {
      AbstractTextUtil.getInstance().typeToInput(Locators.APPLICATION_NAME_FIELD, applicationName, true);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   public String getApplicationName()
   {
      return selenium().getText(Locators.APPLICATION_NAME_FIELD);
   }

   public void selectApplicationType(String type)
   {
      fail();
   }

   public String getApplicationType()
   {
      fail();
      return null;
   }

   public void clickCreateButton() throws InterruptedException
   {
      selenium().click(Locators.CREATE_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public boolean isCreateButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.CREATE_BUTTON + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);      
   }

   public void clickCancelButton() throws InterruptedException
   {
      selenium().click(Locators.CANCEL_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
