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

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfo extends AbstractTestModule
{
   
   private static interface Locators
   {
      
      String USER_INFORMATION_WINDOW = "ideUserInfoView-window";
      
      String LOGIN_FIELD = "ideUserInfoViewLoginField";
      
      String DOMAIN_FIELD = "ideUserInfoViewDomainField";
      
      String APPLICATIONS_LIST_GRID = "ideApplicationGrid";
      
      String APPLICATION_INFO_LIST_GRID = "ideApplicationInfoGrid";
      
      String OK_BUTTON = "ideUserInfoViewOkButton";
      
   }
   
   public void openUserInformationWindow() throws Exception {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.OpenShift.OPENSHIFT,
         MenuCommands.PaaS.OpenShift.USER_INFO);
      waitForElementPresent(Locators.USER_INFORMATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void waitForUserInformationWindowPresent() throws Exception {
      waitForElementPresent(Locators.USER_INFORMATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);      
   }
   
   public void waitForUserInformationWindowNotPresent() throws Exception {
      waitForElementNotPresent(Locators.USER_INFORMATION_WINDOW);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void clickOkButton() throws Exception {
      selenium().click(Locators.OK_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public String getLogin() {
      return selenium().getValue(Locators.LOGIN_FIELD);
   }
   
   public String getDomain() {
      return selenium().getValue(Locators.DOMAIN_FIELD);
   }
   
   /**
    * Gets text from Applications table.
    * 
    * @param row row, starts at 1
    * @param column column, starts at 1
    * @return
    */
   public String getTextFromApplicationsTable(int row, int column) {
      String locator = "//table[@id='ideApplicationGrid']/tbody/tr[" + row + "]/td[" + column + "]/div";
      return selenium().getText(locator);
   }
   
   public void selectApplication(String appName) {
      String locator = "//table[@id='ideApplicationGrid']//div[text()='" + appName + "']";
      selenium().click(locator);
   }
   
   /**
    * Gets text from Application Information table.
    * 
    * @param row row, starts at 1
    * @param column column, starts at 1
    * @return
    */
   public String getTextFromAppInfoTble(int row, int column) {
      String locator = "//table[@id='ide.OpenShift.ApplicationInfo.ListGrid']/tbody/tr[" + row + "]/td[" + column + "]/div";
      return selenium().getText(locator);
   }

}
