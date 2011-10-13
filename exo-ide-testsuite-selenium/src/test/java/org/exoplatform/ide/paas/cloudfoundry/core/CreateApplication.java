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

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateApplication extends AbstractTestModule
{
   
   public interface Locators
   {
      
      String VIEW_ID = "ideCloudFoundryCreateAppView";
      
      String CREATE_BUTTON_ID = "ideCloudFoundryAppViewCreateButton";
      
      String CANCEL_BUTTON_ID = "ideCloudFoundryAppViewCancelButton";
      
      String AUTODETECT_CHECK_BOX = "//span[@id='autodetectCheckBox']/input";
      
      String APPTYPE_SELECTION = "name=ideCloudFoundryAppViewTypeField";
      
   }
   
   public void openCreateApplicationForm() throws Exception {
      IDE().MENU.runCommand("PaaS", "CloudFoundry", "Create Application");
   }
   
   public void waitForFormOpened() throws Exception {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      waitForElementPresent(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void waitForFormClosed() throws Exception {
      String locator = "//div[@view-id='" + Locators.VIEW_ID + "']";
      waitForElementNotPresent(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public String getServerURL() {
      return null;
   }
   
   public void typeServerURL(String url) {
   }
   
   public String getApplicationName() {
      return null;
   }
   
   public void typeApplicationName(String applicationName) {
   }
   
   public void clickAutodetectCheckBox() throws InterruptedException {
      selenium().click(Locators.AUTODETECT_CHECK_BOX);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void selectApplicationType(String value) throws InterruptedException {
      selenium().select(Locators.APPTYPE_SELECTION, "label=" + value);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public boolean isAutodetectChecked() {
      return false;
   }
   
   public String getURL() {
      return null;
   }
   
   public void typeURL(String url) {
   }
   
   public void clickCustomCheckBox() {
   }
   
   public boolean isCustomChecked() {
      return false;
   }
   
   public String getInstancesValue() {
      return null;
   }
   
   public void typeInstancesValue(String instances) {
   }
   
   public String getMemoryValue() {
      return null;
   }
   
   public void typeMemoryValue(String memoryValue) {
   }

   public void clickCreateButton() throws InterruptedException {
      selenium().click(Locators.CREATE_BUTTON_ID);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void clickCancelButton() throws InterruptedException {
      selenium().click(Locators.CANCEL_BUTTON_ID);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
