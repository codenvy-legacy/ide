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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RESTService May 11, 2011 2:29:11 PM evgen $
 *
 */
public class RESTService extends AbstractTestModule
{

   private static final String PATH_SUGGEST_PANEL_TEXT_LOCATOR =
      "//div[@id='exoSuggestPanel']/div[@class='popupContent']/div/table//td[contains(text(), '%1s')]";

   private static final String REST_SERVICE_REQUEST_MEDIATYPE = "ideGroovyServiceRequest";

   public final String REST_SERVICE_METHOD = "ideGroovyServiceMethod";

   private static final String REST_SERVICE_PATH = "ideGroovyServicePath";

   private static final String REST_SERVICE_RESPONSE_MEDIATYPE = "ideGroovyServiceResponse";

   public final String LAUNCH_SEND_BTN = "ideGroovyServiceSend";

   public final String REST_SERVICE_FORM = "ideGroovyServiceForm";

   private static final String TABS_LOCATORS = "//table[@id='ideGroovyServiceTabSet']//td[@tab-bar-index=%1s]/table";

   public final String QUERY_TABLE = "//table[@id='ideGroovyServiceQueryTable']/tbody";

   public final String HEADER_TABLE = "//table[@id='ideGroovyServiceHeaderTable']/tbody";

   private final String SEND_REQUEST_BUTTON = "ideGroovyServiceSend";
   
   public final String BODY_TEXT_FIELD = "ideGroovyServiceBodyFormText";

   /**
    * Call the "Run->Launch REST Service" topmenu command
    * 
    * @throws Exception
    * @throws InterruptedException
    */
   public void launchRestService() throws Exception, InterruptedException
   {
      IDE().TOOLBAR.runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      waitForElementPresent(REST_SERVICE_FORM);
   }

   /**
    * Validate REST Service, and check, that all ok.
    * 
    * @param fileName - name of file
    * @param numberOfRecord - number of notification record if Output Tab (from 0)
    * @throws Exception
    */
   public void validate(String fileName, int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium().isElementPresent(Locators.OperationForm.OUTPUT_TAB_LOCATOR));

      final String msg = getOutputMsgText(numberOfRecord);

      assertEquals("[INFO] " + fileName + " validated successfully.", msg);
   }

   /**
    * @param filePath - path to file in workspace tree
    * (e.g. SampleProject/server.RESTService.grs)
    * @param numberOfRecord - number of notification record if Output Tab (from 0)
    * @throws Exception
    */
   public void deploy(String filePath, int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium().isElementPresent(Locators.OperationForm.OUTPUT_TAB_LOCATOR));

      final String msg = getOutputMsgText(numberOfRecord);

      final String validateSuccessMsg =
         "[INFO] " + BaseTest.ENTRY_POINT_URL + BaseTest.WS_NAME + "/" + filePath + " deployed successfully.";

      assertEquals(validateSuccessMsg, msg);
   }

   //FIXME 
   public String getOutputMsgText(int numberOfRecord)
   {
      //indexes of element in xpath starts from 1, but in out project all indexes start from 0
      final int recordIndex = numberOfRecord + 1;
      return selenium().getText(
         Locators.OperationForm.OUTPUT_FORM_LOCATOR + "/div[contains(@eventproxy, " + "'isc_OutputRecord_')]["
            + recordIndex + "]");
   }

   /**
    * Get Path field value
    * @return Path of REST Service method
    */
   public String getPathFieldValue()
   {
      return selenium().getValue(REST_SERVICE_PATH);
   }

   /**
    * Get Method field value
    * @return Method of REST Service 
    */
   public String getMethodFieldValue()
   {
      return selenium().getValue(REST_SERVICE_METHOD);
   }

   /**
    * Get Request Media Type field value
    * @return request media type field value
    */
   public String getRequestMediaTypeFieldValue()
   {
      return selenium().getValue(REST_SERVICE_REQUEST_MEDIATYPE);
   }

   /**
    * Get Response Media Type field value
    * @return request media type field value
    */
   public String getResponseMediaTypeFieldValue()
   {
      return selenium().getValue(REST_SERVICE_RESPONSE_MEDIATYPE);
   }

   private void clickOnTab(int tabIndex)
   {
      selenium().click(String.format(TABS_LOCATORS, tabIndex));
   }

   /**
    * Select Query Parameter Tab
    */
   public void selectQueryParametersTab()
   {
      clickOnTab(0);
   }

   /**
    * Header Parameter Tab
    */
   public void selectHeaderParametersTab()
   {
      clickOnTab(1);
   }

   /**
    * Select Body Tab
    */
   public void selectBodyTab()
   {
      clickOnTab(2);
   }

   /**
    * Open Path field suggest panel
    * @throws Exception
    */
   public void openPathList() throws Exception
   {
      selenium().click("//table[@id='ideGroovyServiceForm']//img");
      waitForElementPresent("exoSuggestPanel");
   }

   /**
    * Check is Path suggest panel list contains element with text
    * @param text that Path suggest panel must contains 
    */
   public void checkPathListTextPresent(String text)
   {
      String locator = String.format(PATH_SUGGEST_PANEL_TEXT_LOCATOR, text);
      assertTrue(selenium().isElementPresent(locator));
   }

   /**
    * Select item from Path suggest panel
    * @param itemText Path suggest panel item text
    */
   public void selectPathSuggestPanelItem(String itemText)
   {
      String locator = String.format(PATH_SUGGEST_PANEL_TEXT_LOCATOR, itemText);
      selenium().click(locator);
   }

   /**
    * Type text to Path field
    * @param text to type
    */
   public void typeToPathField(String text)
   {
      selenium().type(REST_SERVICE_PATH, text);
   }

   /**
    * Send request via click on "Send: button
    * @throws Exception 
    */
   public void sendRequst() throws Exception
   {
      selenium().click(SEND_REQUEST_BUTTON);
      waitForElementNotPresent(REST_SERVICE_FORM);
   }

   private void checSelectElementContainsValue(String selectLocator, String val[])
   {
      String[] options = selenium().getSelectOptions(selectLocator);
      for (String o : options)
      {
         boolean contais = false;
         for (String v : val)
         {
            if (o.equals(v))
            {
               contais = true;
               break;
            }
         }
         assertTrue(contais);
      }
   }
   
   private void selectValueInSelectElement(String selectLocator, String value)
   {
      selenium().select(selectLocator, value);
   }

   /**
    * Check is Request media type field has values
    * @param val  Request media type field values
    */
   public void checkRequestFieldContainsValues(String... val)
   {
      checSelectElementContainsValue(REST_SERVICE_REQUEST_MEDIATYPE, val);
   }
   
   /**
    * Select specific value in Request Media Type Field.
    * @param value To select. <b>Value must contains in Select field</b>
    */
   public void setRequestMediaTypeFieldValue(String value)
   {
      selectValueInSelectElement(REST_SERVICE_REQUEST_MEDIATYPE, value);
   }

}
