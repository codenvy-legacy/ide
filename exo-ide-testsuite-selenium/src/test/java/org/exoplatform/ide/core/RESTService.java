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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RESTService May 11, 2011 2:29:11 PM evgen $
 *FIXME
 */
public class RESTService extends AbstractTestModule
{
   private interface Locators
   {
      String RESTSERVICE_FORM = "ideGroovyServiceOutputPreviewForm-window";

      String REST_SERVICE_GET_URL_FORM = "ideGetRestServiceURLForm-window";

      String REST_SERVICE_METHOD = "ideGroovyServiceMethod";

      String LAUNCH_SEND_BTN = "ideGroovyServiceSend";

      String GET_URL_BTN = "ideGroovyServiceGetURL";

      String GET_URL_FORM_FIELD = "ideGetItemURLFormURLField";

      String GET_URL_FORM_OK_BUTTON = "ideGetRestServiceURLFormOkButton";

      String REST_SERVICE_FORM = "ideGroovyServiceForm";

      String BODY_TEXT_FIELD = "ideGroovyServiceBodyFormText";

      String REST_SERVICE_PATH = "ideGroovyServicePath";

      String REST_SERVICE_REQUEST_MEDIATYPE = "ideGroovyServiceRequest";

      String REST_SERVICE_RESPONSE_MEDIATYPE = "ideGroovyServiceResponse";

      String QUERY_TABLE_ID = "ideGroovyServiceQueryTable";

      String HEADER_TABLE_ID = "ideGroovyServiceHeaderTable";

      String TABS_LOCATOR =
         "//div[@id='ideGroovyServiceTabSet']//div[@class='gwt-TabLayoutPanelTabs']//div[@class='gwt-TabLayoutPanelTab GGUO0GHH' and @tab-bar-index=%1s]";

      String CHECK_SELECTED_TABS_LOCATORS =
         "//div[@id='ideGroovyServiceTabSet']//div[@class=\"gwt-TabLayoutPanelTab GGUO0GHH gwt-TabLayoutPanelTab-selected\"and @tab-bar-index=%1s]";

      String SEND_REQUEST_BUTTON = "ideGroovyServiceSend";

      String CANCEL_BUTTON = "ideGroovyServiceCancel";

      String PATH_LIST_OPEN = "//table[@id='ideGroovyServiceForm']//img";

      String DROP_DOWN_PATH_LIST = "exoSuggestPanel";

      String TABLE_SELECT_PREFIX = "//table[@id='%1s']/tbody/tr[%2s]/td[%3s]/div";

      String TABLE_VALUE_PREFIX = "//table[@id='%1s']/tbody/tr[%2s]/td[%3s]//div//input";

   }

   // The basic webelements of the launch 
   // Resrservice form: 
   @FindBy(id = Locators.QUERY_TABLE_ID)
   private WebElement queryTable;

   @FindBy(id = Locators.HEADER_TABLE_ID)
   private WebElement headerTable;

   @FindBy(name = Locators.BODY_TEXT_FIELD)
   private WebElement bodyTable;

   @FindBy(id = Locators.REST_SERVICE_FORM)
   private WebElement restServiceForm;

   @FindBy(id = Locators.CANCEL_BUTTON)
   private WebElement cancelButton;

   @FindBy(id = Locators.SEND_REQUEST_BUTTON)
   private WebElement sendButton;

   @FindBy(name = Locators.REST_SERVICE_PATH)
   private WebElement restServicePath;

   @FindBy(name = Locators.REST_SERVICE_METHOD)
   private WebElement restServiceMethod;

   @FindBy(name = Locators.REST_SERVICE_REQUEST_MEDIATYPE)
   private WebElement restServiceRequestMediaType;

   @FindBy(name = Locators.REST_SERVICE_RESPONSE_MEDIATYPE)
   private WebElement restServiceResponseMediaType;

   @FindBy(xpath = Locators.PATH_LIST_OPEN)
   private WebElement pahtListOpen;

   @FindBy(id = Locators.DROP_DOWN_PATH_LIST)
   private WebElement dropDownPahtList;

   @FindBy(id = Locators.HEADER_TABLE_ID)
   private WebElement headerParametrTabble;

   @FindBy(id = Locators.REST_SERVICE_GET_URL_FORM)
   private WebElement getUrlForm;

   @FindBy(name = Locators.GET_URL_FORM_FIELD)
   private WebElement getUrlFormField;

   @FindBy(id = Locators.GET_URL_BTN)
   private WebElement getUrlButton;

   @FindBy(id = Locators.GET_URL_FORM_OK_BUTTON)
   private WebElement getUrlFormOkBtn;

   /**
    * Wait appearance REST Service Form
   * 
   */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return restServiceForm != null && restServiceForm.isDisplayed();
         }
      });
   }

   /**
    * Wait disappearance REST Service Form
   *  @throws InterruptedException
   */
   public void waitClosed() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(Locators.REST_SERVICE_FORM));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
   * Wait appearance pathlistCombobox
   * 
   * 
   */
   public void waitPathListOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return dropDownPahtList != null && dropDownPahtList.isDisplayed();
         }
      });
   }

   /**
    * Wait appearance selected tab
   * @throws InterruptedException
   */
   public void waitRestServiceTabOpened(final int tabIndex) throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement viewTab =
                  input.findElement(By.xpath(String.format(Locators.CHECK_SELECTED_TABS_LOCATORS, tabIndex)));
               return (viewTab != null && viewTab.isDisplayed());

            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait while GET URL REST Service Form closed
   * @throws InterruptedException
   */
   public void waitGetUrlRestServiceOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {

            return getUrlForm != null && getUrlForm.isDisplayed();

         }
      });
   }

   /**
    * Wait while GET URL REST Service Form closed
   * @throws InterruptedException
   */
   public void waitGetUrlRestServiceClosed() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement view = input.findElement(By.id(Locators.REST_SERVICE_GET_URL_FORM));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Wait appearance Field for type value 
   * @throws InterruptedException
   */
   public void waitValueOpened(final WebElement table, final String xPathvalueField) throws InterruptedException
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {

            try
            {
               WebElement view = table.findElement(By.xpath(xPathvalueField + "/div/input"));
               return (view != null && view.isDisplayed());
            }

            catch (Exception e)
            {
               return false;
            }

         }
      });
   }

   /**
    * Call the "Run->Launch REST Service" topmenu command
    * 
    * @throws Exception
    * @throws InterruptedException
    */

   public void launchRestService() throws Exception, InterruptedException
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.Run.LAUNCH_REST_SERVICE);
      waitOpened();
   }

   /**
    * @param filePath - path to file in workspace tree
    * (e.g. SampleProject/server.RESTService.grs)
    * @param numberOfRecord - number of notification record if Output Tab (from 1)
    * @throws Exception
    */
   public String deploy(String filePath, int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE().OUTPUT.waitOpened();
      IDE().OUTPUT.waitForMessageShow(numberOfRecord, 4);
      final String msg = IDE().OUTPUT.getOutputMessage(numberOfRecord);
      final String validateSuccessMsg =
         "[INFO] " + BaseTest.ENTRY_POINT_URL_IDE + BaseTest.WS_NAME + "/" + " deployed successfully.";

      // TODO After resolve issue IDE-1370 previouse string "validateSuccessMsg" should be removed and uncomment this string
      //final String validateSuccessMsg =
      //   "[INFO] " + BaseTest.ENTRY_POINT_URL_IDE + BaseTest.WS_NAME + "/" + filePath + " deployed successfully.";

      return msg;
   }

   /**
      * Validate REST Service, and check, that all ok.
      * 
      * @param fileName - name of file
      * @param numberOfRecord - number of notification record if Output Tab (from 1)
      * @throws Exception
      */
   public String validate(int numberOfRecord) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE);
      IDE().OUTPUT.waitOpened();
      assertTrue(IDE().OUTPUT.isOpened());
      IDE().OUTPUT.waitForMessageShow(numberOfRecord);
      final String msg = IDE().OUTPUT.getOutputMessage(numberOfRecord);
      return msg;

   }

   /**
    * Close Launch REST Service form, by press Cancel button
    */
   public void closeForm()
   {
      cancelButton.click();
   }

   /**
    * Get Path field value
    * @return Path of REST Service
    */
   public String getPathFieldValue()
   {
      return IDE().INPUT.getValue(restServicePath);
   }

   /**
    * Get Method field value
    * @return Method of REST Service 
    */
   public String getMethodFieldValue()
   {
      return IDE().INPUT.getValue(restServiceMethod);
   }

   /**
    * Get Request Media Type field value
    * @return request media type field value
    */
   public String getRequestMediaTypeFieldValue()
   {
      return IDE().INPUT.getValue(restServiceRequestMediaType);
   }

   /**
    * Get Response Media Type field value
    * @return request media type field value
    */
   public String getResponseMediaTypeFieldValue()
   {
      return IDE().INPUT.getValue(restServiceResponseMediaType);

   }

   /**
    * @param tabIndex
    * Click on  tab of the RestService Form and
    * check selected tab
    * numbering begins with zero
    */
   private void clickOnTab(int tabIndex)
   {

      WebElement tab = driver().findElement(By.xpath(String.format(Locators.TABS_LOCATOR, tabIndex)));
      tab.click();

      try
      {
         waitRestServiceTabOpened(tabIndex);
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
    * Check is Path suggest panel list contains element with text
    * @param text that Path suggest panel must contains 
    */
   public boolean isPathListTextPresent(String text)
   {
      return IDE().INPUT.isComboboxValuePresent(restServicePath, text);
   }

   /**
    * Send request via click on "Send" button
    * @throws Exception 
    */
   public void sendRequst() throws Exception
   {
      clickSendButton();
      waitClosed();
   }

   /**
    * Click on "Send" button
    */
   public void clickSendButton()
   {
      sendButton.click();
   }

   /**
    * @param selectLocator
    * @param val
    * @return
    * Check contains element in drop down fields RestService form
    * if drop down in RestService form is contains value return true
    */
   public boolean isSelectElementContainsValue(WebElement selectElement, String val[])
   {
      List<WebElement> allOptions = selectElement.findElements(By.tagName("option"));

      boolean contains = false;

      for (String v : val)
      {
         for (WebElement option : allOptions)
         {
            if (option.getText().equals(v))
            {
               contains = true;
               break;
            }
         }
         if (!contains)
         {
            return false;
         }
      }
      return contains;
   }

   /**
    * Check is Request media type field has values
    * @param val  Request media type field values
    */
   public boolean isRequestFieldContainsValues(String... val)
   {
      return isSelectElementContainsValue(restServiceMethod, val);
   }

   /**
    * selectElement in drop down field
    * @param 
    * @param value
    */
   private void selectValueInSelectElement(WebElement selectElement, String value)
   {
      Select select = new Select(selectElement);
      select.selectByVisibleText(value);
   }

   /**
   * Select specific value in Request Media Type Field.
   * @param value
   */
   public void setRequestMediaTypeFieldValue(String value)
   {
      selectValueInSelectElement(restServiceRequestMediaType, value);
   }

   /**
    * Select specific value in Request Media Type Field.
    * @param value
    */
   public void setResponceMediaTypeFieldValue(String value)
   {
      selectValueInSelectElement(restServiceResponseMediaType, value);
   }

   /**
    * Select specific value in Method Filed
    * @param value to select.
    */
   public void setMethodFieldValue(String value)
   {
      selectValueInSelectElement(restServiceMethod, value);
   }

   /**
    * return value (text) from Rest Service parameter table
    * @param tableId
    * @param rowIndex
    * @param cellIndex
    * @return 
    * 
    */
   private String getTableValue(String tableId, int rowIndex, int cellIndex)
   {
      String locator = String.format(Locators.TABLE_SELECT_PREFIX, tableId, rowIndex, cellIndex);
      WebElement tableValue = driver().findElement(By.xpath(locator));
      return tableValue.getText();
   }

   /**
    * Get Query parameter name
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter name
    */
   public String getQueryParameterName(int parameterIndex)
   {
      return getTableValue(Locators.QUERY_TABLE_ID, parameterIndex, 2);
   }

   /**
    * Get Query parameter type
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter type
    */
   public String getQueryParameterType(int parameterIndex)
   {
      return getTableValue(Locators.QUERY_TABLE_ID, parameterIndex, 3);
   }

   /**
    * Get Query parameter default value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter default value
    */
   public String getQueryParameterDefaultValue(int parameterIndex)
   {
      return getTableValue(Locators.QUERY_TABLE_ID, parameterIndex, 4);
   }

   /**
    * Get Query parameter value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Query parameter value
    */
   public String getQueryParameterValue(int parameterIndex)
   {
      return getTableValue(Locators.QUERY_TABLE_ID, parameterIndex, 5);
   }

   /**
    * Get Header parameter name
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter name
    */
   public String getHeaderParameterName(int parameterIndex)
   {
      return getTableValue(Locators.HEADER_TABLE_ID, parameterIndex, 2);
   }

   /**
    * Get Header parameter type
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter type
    */
   public String getHeaderParameterType(int parameterIndex)
   {
      return getTableValue(Locators.HEADER_TABLE_ID, parameterIndex, 3);
   }

   /**
    * Get Header parameter default value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter default value
    */
   public String getHeaderParameterDefaultValue(int parameterIndex)
   {
      return getTableValue(Locators.HEADER_TABLE_ID, parameterIndex, 4);
   }

   /**
    * Get Header parameter value
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @return Header parameter value
    */
   public String getHeaderParameterValue(int parameterIndex)
   {
      return getTableValue(Locators.HEADER_TABLE_ID, parameterIndex, 5);
   }

   private void changeTableCheckBox(WebElement parameter, boolean check)
   {
      WebElement checkBox = parameter.findElement(By.tagName("input"));
      if (check && (!checkBox.isSelected()))
      {
         checkBox.click();
      }
      else if (!check && (checkBox.isSelected()))
      {
         checkBox.click();
      }
   }

   /**
    * Change Header parameter Send check box state  
    * @param parameterIndex parameter index (Parameter index starts from <b>1</b>)
    * @param check check box state (checked/ unchecked)
    */
   public void changeHeaderParameterSendCheckBoxState(boolean check)
   {
      changeTableCheckBox(headerParametrTabble, check);
   }

   /**
    * Open Get URL form, by press Get URL button
    * @throws Exception
    */
   public void openGetURLForm() throws Exception
   {
      getUrlButton.click();
      waitGetUrlRestServiceOpened();
   }

   /**
    * Get REST Service URL field value
    * @return
    */
   public String getUrlFromGetURLForm()
   {
      return getUrlFormField.getAttribute("value");
   }

   /**
    * Close Get URL form, by press Ok button
    * @throws Exception
    */
   public void closeGetURLForm() throws Exception
   {
      getUrlFormOkBtn.click();
      waitGetUrlRestServiceClosed();
   }

   /**
    * check is path present int combobbox of PathList
    * @param pathValue
    * @throws Exception
    */
   public void isValuePresentInPathList(String pathValue) throws Exception
   {
      IDE().INPUT.isComboboxValuePresent(restServicePath, pathValue);
   }

   /**
    * select path in combobox of pathlist
    * @param pathValue
    * @throws Exception
    */
   public void selectInPathList(String pathValue) throws Exception
   {
      IDE().INPUT.selectComboboxValue(restServicePath, pathValue);
   }

   /**
    * Type parameters in value field ot the REST service table  
    * @param tableId
    * @param row
    * @param col
    * @throws InterruptedException
    */
   private void typeToTableValue(WebElement tableId, int row, int col, String valueText) throws InterruptedException
   {
      String valuePrefix = String.format("tbody/tr[%1s]/td[%2s]", row, col);
      WebElement valueColumn = tableId.findElement(By.xpath(valuePrefix));
      valueColumn.click();
      waitValueOpened(tableId, valuePrefix);
      WebElement valueInput = tableId.findElement(By.xpath(valuePrefix + "/div/input"));
      valueInput.sendKeys(valueText);
      Thread.sleep(3000);
   }

   public void typeToPathField(String value) throws InterruptedException
   {
      restServicePath.clear();
      restServicePath.sendKeys(value);

   }

   /**
    * Type new value to Header parameter
    * @param parameterIndex of parameter
    * @param value new value
    * @throws InterruptedException 
    */
   public void typeToHeaderParameterValue(int parameterIndex, String value) throws InterruptedException
   {
      typeToTableValue(headerTable, parameterIndex, 5, value);
   }

   /**
    * Type new value to Query parameter
    * @param parameterIndex nidex of the parameter
    * @param value new value
    * @throws InterruptedException 
    */
   public void typeToQueryParameterValue(int parameterIndex, String value) throws InterruptedException
   {
      typeToTableValue(queryTable, parameterIndex, 5, value);
   }

   /**
    * Type text to body field
    * @param text to type
    */
   public void typeToBodyField(String text)
   {
      bodyTable.sendKeys(text);
   }

   /**
    * Run REST Service and wait for Launch REST Service opened in sandbox
    * @throws Exception
    */
   public void runRESTServiceInSanbox() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      waitOpened();
   }

   /**
    * @return false if REST service form is closed
    * return false if REST service form is open
    */
   public boolean isFormNotOpened()
   {

      try
      {
         if (restServiceForm != null && restServiceForm.isDisplayed())
            ;
         return false;
      }
      catch (Exception e)
      {
         return true;
      }

   }

   /**
    * @return false if REST service form is closed
    * return true if REST service form is open
    */
   public boolean isFormOpened()
   {

      return (restServiceForm != null && restServiceForm.isDisplayed() && restServicePath != null
         && restServicePath.isDisplayed() && restServiceMethod != null && restServiceMethod.isDisplayed()
         && restServiceRequestMediaType != null && restServiceRequestMediaType.isDisplayed()
         && restServiceResponseMediaType != null && restServiceResponseMediaType.isDisplayed() && queryTable != null
         && getUrlButton != null && getUrlButton.isDisplayed() && sendButton != null && sendButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed());

   }

}
