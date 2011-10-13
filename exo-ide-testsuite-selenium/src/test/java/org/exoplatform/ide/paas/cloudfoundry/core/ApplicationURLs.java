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
import org.exoplatform.ide.utils.AbstractTextUtil;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationURLs extends AbstractTestModule
{
   
   public interface Locators
   {
      
      String VIEW_LOCATOR = "//div[@view-id='ideCloudFoundryApplicationInfoView']";
      
      String CLOSE_BUTTON_ID = "applicationURLsCloseButton";
      
      String URL_FIELD_LOCATOR = "//div[@id='applicationURLsURLField']/input";
      
      String MAP_BUTTON_ID = "applicationURLsMapButton";
      
      String URLS_TABLE_ID = "ideCloudFoundryRegisteredUrlsGridView";
      
   }
   
   public void openFromMenu() throws Exception {
      IDE().MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.CloudFoundry.CLOUDFOUNDRY, MenuCommands.PaaS.CloudFoundry.APPLICATION_URLS);
   }
   
   public void waitApplicationURLsOpened() throws Exception {
      waitForElementPresent(Locators.VIEW_LOCATOR);
   }
   
   public void waitApplicationURLsClosed() throws Exception {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
   }
   
   public void clickCloseButton() {
      selenium().click(Locators.CLOSE_BUTTON_ID);
   }
   
   public void typeMapURL(String url) throws Exception {
      AbstractTextUtil.getInstance().typeToInput(Locators.URL_FIELD_LOCATOR, url, true);
   }
   
   public void clickMapButton() {
      selenium().click(Locators.MAP_BUTTON_ID);
   }
   
   /**
    * Returns the amount of URLs
    * 
    * @return amount of URls
    */
   public int getURLsCount() {
      int amount = 0;
      for (int i = 1; i < 1000; i++) {
         String locator = "//table[@id='" + Locators.URLS_TABLE_ID + "']/tbody/tr[" + i + "]";
         if (!selenium().isElementPresent(locator)) {
            break;
         }
         
         amount++;
      }
      
      return amount;
   }
   
   /**
    * 
    * Click on Unmap button
    * 
    * @param row row, starts at 1
    */
   public void clickUnMapButton(int row) {
      String locator = "//table[@id='" + Locators.URLS_TABLE_ID + "']/tbody/tr[" + row + "]/td[2]/div/button";
      selenium().click(locator);
   }
   
   /**
    * Returns URL by row's index
    * 
    * @param row row, starts at 1
    * @return url value in specified row
    */
   public String getURL(int row) {
      String locator = "//table[@id='" + Locators.URLS_TABLE_ID + "']/tbody/tr[" + row + "]/td[1]/div/a";
      return selenium().getText(locator);
   }

}
