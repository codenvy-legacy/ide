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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;

import java.awt.event.KeyEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RESTService May 11, 2011 2:29:11 PM evgen $
 *
 */
public class RESTService extends AbstractTestModule
{
   /**
    * 
    */
   private static final String GET_REST_SERVICE_URL_FORM = "ideGetRestServiceURLForm";

   public final String REST_SERVICE_METHOD = "ideGroovyServiceMethod";

   public final String LAUNCH_SEND_BTN = "ideGroovyServiceSend";

   public final String REST_SERVICE_FORM = "ideGroovyServiceForm";

   public final String QUERY_TABLE = "//table[@id='ideGroovyServiceQueryTable']/tbody";

   public final String HEADER_TABLE = "//table[@id='ideGroovyServiceHeaderTable']/tbody";

   public final String BODY_TEXT_FIELD = "ideGroovyServiceBodyFormText";

   public final String REST_SERVICE_PATH = "ideGroovyServicePath";

   private static final String PATH_SUGGEST_PANEL_TEXT_LOCATOR =
      "//div[@id='exoSuggestPanel']/div[@class='popupContent']/div/table//td[contains(text(), '%1s')]";

   private static final String REST_SERVICE_REQUEST_MEDIATYPE = "ideGroovyServiceRequest";

   private static final String REST_SERVICE_RESPONSE_MEDIATYPE = "ideGroovyServiceResponse";

   private static final String QUERY_TABLE_ID = "ideGroovyServiceQueryTable";

   private static final String HEADER_TABLE_ID = "ideGroovyServiceHeaderTable";

   private static final String TABS_LOCATORS = "//table[@id='ideGroovyServiceTabSet']//td[@tab-bar-index=%1s]/table";

   private static final String SEND_REQUEST_BUTTON = "ideGroovyServiceSend";

   private static final String CANCEL_BUTTON = "ideGroovyServiceCancel";

   /**
    * Call the "Run->Launch REST Service" topmenu command
    * 
    * @throws Exception
    * @throws InterruptedException
    */
   public void launchRestService() throws Exception, InterruptedException
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.Run.LAUNCH_REST_SERVICE);
      waitForElementPresent(REST_SERVICE_FORM);
   }

   /**
    * Wait for Launch REST Service view to be opened.
    * 
    * @throws Exception
    */
   public void waitForLaunchRestServiceViewOpened() throws Exception
   {
      waitForElementPresent(REST_SERVICE_FORM);
   }

   /**
    * Wait for Launch REST Service view to be closed.
    * 
    * @throws Exception
    */
   public void waitForLaunchRestServiceViewClosed() throws Exception
   {
      waitForElementNotPresent(REST_SERVICE_FORM);
   }

   /**
    * Validate REST Service, and check, that all ok.
    * 
    * @param fileName - name of file
    * @param numberOfRecord - number of notification record if Output Tab (from 1)
    * @throws Exception
    */
   public void validate(String fileName, int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE);
      IDE().OUTPUT.waitOpened();
      assertTrue(IDE().OUTPUT.isOpened());
      
      IDE().OUTPUT.waitForMessageShow(numberOfRecord);
      final String msg = IDE().OUTPUT.getOutputMessage(numberOfRecord);
      assertEquals("[INFO] " + fileName + " validated successfully.", msg);
   }

   /**
    * @param filePath - path to file in workspace tree
    * (e.g. SampleProject/server.RESTService.grs)
    * @param numberOfRecord - number of notification record if Output Tab (from 1)
    * @throws Exception
    */
   public void deploy(String filePath, int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE().OUTPUT.waitOpened();
      IDE().OUTPUT.waitForMessageShow(numberOfRecord);

      final String msg = IDE().OUTPUT.getOutputMessage(numberOfRecord);

      final String validateSuccessMsg =
         "[INFO] " + BaseTest.ENTRY_POINT_URL_IDE + BaseTest.WS_NAME + "/" + filePath + " deployed successfully.";

      assertEquals(validateSuccessMsg, msg);
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
      try
      {
         waitForElementPresent(String.format(TABS_LOCATORS, tabIndex));
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
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
    * Send request via click on "Send" button
    * @throws Exception 
    */
   public void sendRequst() throws Exception
   {
      clickSendButton();
      waitForElementNotPresent(REST_SERVICE_FORM);
   }

   /**
    * Click on "Send" button
    */
   public void clickSendButton()
   {
      selenium().click(SEND_REQUEST_BUTTON);
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

   /**
    * Select specific value in Method Filed
    * @param value to select.
    */
   public void setMethodFieldValue(String value)
   {
      selectValueInSelectElement(REST_SERVICE_METHOD, value);
   }

   private String getTableValue(String tableId, int rowIndex, int cellIndex)
   {
      String locator = String.format("//table[@id='%1s']/tbody/tr[%2s]/td[%3s]/div", tableId, rowIndex, cellIndex);
      return selenium().getText(locator);
   }

   /**
    * Get Query parameter name
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter name
    */
   public String getQueryParameterName(int parameterIndex)
   {
      return getTableValue(QUERY_TABLE_ID, parameterIndex, 2);
   }

   /**
    * Get Query parameter type
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter type
    */
   public String getQueryParameterType(int parameterIndex)
   {
      return getTableValue(QUERY_TABLE_ID, parameterIndex, 3);
   }

   /**
    * Get Query parameter default value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter default value
    */
   public String getQueryParameterDefaultValue(int parameterIndex)
   {
      return getTableValue(QUERY_TABLE_ID, parameterIndex, 4);
   }

   /**
    * Get Query parameter value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter value
    */
   public String getQueryParameterValue(int parameterIndex)
   {
      return getTableValue(QUERY_TABLE_ID, parameterIndex, 5);
   }

   /**
    * Get Header parameter name
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter name
    */
   public String getHeaderParameterName(int parameterIndex)
   {
      return getTableValue(HEADER_TABLE_ID, parameterIndex, 2);
   }

   /**
    * Get Header parameter type
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter type
    */
   public String getHeaderParameterType(int parameterIndex)
   {
      return getTableValue(HEADER_TABLE_ID, parameterIndex, 3);
   }

   /**
    * Get Header parameter default value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter default value
    */
   public String getHeaderParameterDefaultValue(int parameterIndex)
   {
      return getTableValue(HEADER_TABLE_ID, parameterIndex, 4);
   }

   /**
    * Get Header parameter value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter value
    */
   public String getHeaderParameterValue(int parameterIndex)
   {
      return getTableValue(HEADER_TABLE_ID, parameterIndex, 5);
   }

   /**
    * Close Launch REST Service form, by press Cancel button
    */
   public void closeForm()
   {
      selenium().click(CANCEL_BUTTON);
   }

   private void changeTableCheckBox(String tableId, int row, int col, boolean check)
   {
      String locator =
         String.format("//table[@id='%1s']/tbody/tr[%2s]/td[%3s]/div/input[@type='checkbox']", tableId, row, col);

      selenium().click(String.format("//table[@id='%1s']/tbody/tr[%2s]/td[%3s]/div", tableId, row, col));
      selenium().click(locator);
      if (check)
         selenium().check(locator);
      else
         selenium().uncheck(locator);

   }

   /**
    * Change Header parameter Send check box state  
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @param check check box state (checked/ unchecked)
    */
   public void changeHeaderParameterSendCheckBoxState(int parameterIndex, boolean check)
   {
      changeTableCheckBox(HEADER_TABLE_ID, parameterIndex, 1, check);
   }

   /**
    * Open Get URL form, by press Get URL button
    * @throws Exception
    */
   public void openGetURLForm() throws Exception
   {
      selenium().click("ideGroovyServiceGetURL");
      waitForElementPresent(GET_REST_SERVICE_URL_FORM);
   }

   /**
    * Get REST Service URL field value
    * @return
    */
   public String getUrlFromGetURLForm()
   {
      return selenium().getValue("ideGetItemURLFormURLField");
   }

   /**
    * Close Get URL form, by press Ok button
    * @throws Exception
    */
   public void closeGetURLForm() throws Exception
   {
      selenium().click("ideGetRestServiceURLFormOkButton");
      waitForElementNotPresent(GET_REST_SERVICE_URL_FORM);
   }

   /**
    * Select specific value from Path combobox
    * @param pathValue value to select
    * @throws Exception
    */
   public void selectPathValue(String pathValue) throws Exception
   {
      openPathList();
      selectPathSuggestPanelItem(pathValue);
   }

   private void typeToTableValue(String tableId, int row, int col, String value)
   {
      
     // added waiting methods, for redrawing and appearance all elements table "LaunchRESTService"
      String locator = String.format("//table[@id='%1s']/tbody/tr[%2s]/td[%3s]/div", tableId, row, col);
      try
      {
         waitForElementPresent(locator);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      selenium().click(locator);
      
      //add click, This method fixes the problem call of input field for write a value in HeaderParameter table
      selenium().click(locator);
            
      try
      {
         waitForElementPresent(locator);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      assertTrue(selenium().isElementPresent(locator));
      selenium().focus(locator);
      selenium().typeKeys(locator, value);
      selenium().keyPressNative("" + KeyEvent.VK_ENTER);
      assertFalse(selenium().isElementPresent(locator));

   }

   /**
    * Type new value to Header parameter
    * @param parameterIndex of parameter
    * @param value new value
    */
   public void typeToHeaderParameterValue(int parameterIndex, String value)
   {
      typeToTableValue(HEADER_TABLE_ID, parameterIndex, 5, value);
   }

   /**
    * Type new value to Query parameter
    * @param parameterIndex nidex of the parameter
    * @param value new value
    */
   public void typeToQueryParameterValue(int parameterIndex, String value)
   {
      typeToTableValue(QUERY_TABLE_ID, parameterIndex, 5, value);
   }

   /**
    * Type text to body field
    * @param text to type
    */
   public void typeToBodyField(String text)
   {
      selenium().typeKeys(BODY_TEXT_FIELD, text);
   }

   /**
    * Run REST Service and wait for Launch REST Service opened
    * @throws Exception
    */
   public void runRESTService() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      waitForElementPresent(REST_SERVICE_FORM);
   }

   public void checkIsFormNotOpened()
   {
      assertFalse(selenium().isElementPresent(REST_SERVICE_FORM));
   }

   /**
    * 
    */
   public void checkIsFormOpened()
   {
      assertTrue(selenium().isElementPresent(REST_SERVICE_FORM));
   }
}
