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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.IDE;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Utils to work with REST Services.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 14, 2011 $
 *
 */
public class RestServiceUtils
{
   private static final Selenium selenium;
   
   private static final IDE ide;
   
   public interface Locators_Rest
   {
      public static final String SC_LAUNCH_SEND_BTN = "scLocator=//IButton[ID=\"ideGroovyServiceSend\"]";
   }
   
   static
   {
      selenium = BaseTest.selenium;
      ide = BaseTest.IDE;
   }
   
   /**
    * Validate REST Service, and check, that all ok.
    * 
    * @param fileName - name of file
    * @param numberOfRecord - number of notification record if Output Tab (from 0)
    * @throws Exception
    */
   public static void validate(String fileName, int numberOfRecord) throws Exception
   {
      IDE.getInstance().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent(Locators.OperationForm.OUTPUT_TAB_LOCATOR));
      
      final String msg = getOutputMsgText(numberOfRecord);
      
      assertEquals("[INFO] " + fileName + " validated successfully.", msg);
   }
   
   /**
    * @param filePath - path to file in workspace tree
    * (e.g. SampleProject/server.RESTService.grs)
    * @param numberOfRecord - number of notification record if Output Tab (from 0)
    * @throws Exception
    */
   public static void deploy(String filePath, int numberOfRecord) throws Exception
   {
      IDE.getInstance().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent(Locators.OperationForm.OUTPUT_TAB_LOCATOR));
      
      final String msg = getOutputMsgText(numberOfRecord);
      
      final String validateSuccessMsg =
         "[INFO] " + BaseTest.ENTRY_POINT_URL + BaseTest.WS_NAME + "/" + filePath + " deployed successfully.";
      
      assertEquals (validateSuccessMsg, msg);
   }
   
   public static String getOutputMsgText(int numberOfRecord)
   {
      //indexes of element in xpath starts from 1, but in out project all indexes start from 0
      final int recordIndex = numberOfRecord + 1;
      return selenium.getText(Locators.OperationForm.OUTPUT_FORM_LOCATOR + "/div[contains(@eventproxy, " 
         + "'isc_OutputRecord_')][" + recordIndex + "]");
   }

}
