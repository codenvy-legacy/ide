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

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateDomain extends AbstractTestModule
{

   public static interface Locators
   {

      String CREATE_DOMAIN_WINDOW = "ideCreateDomainView-window";

      String DOMAIN_NAME_FIELD = "ideCreateDomainViewNameField";

      String CREATE_BUTTON = "ideCreateDomainViewCreateButton";

      String CANCEL_BUTTON = "ideCreateDomainViewCancelButton";

   }

   public void openCreateDomainWindow() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.OpenShift.OPENSHIFT,
         MenuCommands.PaaS.OpenShift.CREATE_DOMAIN);
      waitForElementPresent(Locators.CREATE_DOMAIN_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void waitForCreateDomainWindowPresent() throws Exception
   {
      waitForElementPresent(Locators.CREATE_DOMAIN_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void waitForCreateDomainWindowNotPresent() throws Exception
   {
      waitForElementNotPresent(Locators.CREATE_DOMAIN_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void typeDomainName(String domainName) throws Exception
   {
      IDE().INPUT.typeToElement(driver().findElement(By.name(Locators.DOMAIN_NAME_FIELD)), domainName, true);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   public void clickCreateButton() throws Exception
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
