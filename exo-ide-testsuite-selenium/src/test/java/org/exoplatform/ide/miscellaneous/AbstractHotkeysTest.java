/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.After;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public abstract class AbstractHotkeysTest extends BaseTest
{
   static final int NUMBER_OF_COMMANDS = 500;
   
   static final String GOOGLE_GADGET_FILE = "GoogleGadget.xml";
   
   static final String DEFAULT_TEXT_IN_GADGET = "Hello, world!";

   static String FOLDER_NAME;
   
   static final String INFO_MESSAGE_STYLE = "exo-cutomizeHotKey-label-info";
   
   static final String ERROR_MESSAGE_STYLE = "exo-cutomizeHotKey-label-error";
   
   static final String BIND_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/";

   static final String SAVE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/";

   static final String CUSTOMIZE_HOTKEYS_FORM_LOCATOR = "scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/";

   static final String UNBIND_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCustomizeHotKeysFormUnbindButton\"]/";

   static final String CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCustomizeHotKeysFormCancelButton\"]/";
   
   static final String TEXT_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideCustomizeHotKeysFormDynamicFormHotKeyField\"]" 
      + "/item[name=ideCustomizeHotKeysFormHotKeyField]/element";
   
   static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" 
   + WS_NAME + "/";

   String locator;
   
   interface Commands
   {
      public static final String CREATE_FILE_FROM_TEMPLATE = "Create File From Template...";
      
      public static final String NEW_CSS_FILE = "New CSS File";
      
      public static final String NEW_TEXT_FILE = "New TEXT File";
      
      public static final String NEW_HTML_FILE = "New HTML File";
   }
   
   @After
   public void tearDown()
   {
      cleanRegistry();
      deleteCookies();
   }
   
   void refresh() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   void selectRow(String rowTitle) throws Exception
   {
      String locator = getRowLocator(rowTitle);
      if (locator == null)
      {
         fail("Can't find locator " + locator + " in list");
      }
      selenium.click(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   void deselectRow(String rowTitle) throws Exception
   {
      locator = getRowLocator(Commands.NEW_CSS_FILE);
      if (locator == null)
      {
         fail("Can't find locator " + locator + " in list");
      }
      selenium.controlKeyDown();
      selenium.click(locator);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   String getRowLocator(String name) throws Exception
   {
      String locator = null;
      
      for (int rowNumber = 0; rowNumber < NUMBER_OF_COMMANDS; rowNumber++)
      {
         locator = "scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[" 
            + String.valueOf(rowNumber) + "]/col[0]";
         
         if (selenium.isElementPresent(locator))
         {
            String text = selenium.getText(locator);
            if (text.equals(name))
            {
               return locator;
            }
         }
         else
         {
            return null;
         }
      }
      return null;
   }
   
   void openFolder() throws Exception
   {
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
   }
   
   /**
    * Click on close button of form.
    * 
    * @param locator locator of form
    * @throws Exception
    */
   void closeForm(String locator) throws Exception
   {
      if (!locator.endsWith("/"))
      {
         locator += "/";
      }
      selenium.click(locator + "closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   void clickButton(String buttonLocator) throws Exception
   {
      selenium.click(buttonLocator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   void checkNoMessage() throws Exception
   {
      String msgLocator = "//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error']";
      if (selenium.isElementPresent(msgLocator))
      {
         assertEquals("", selenium.getText(msgLocator));
      }
      msgLocator = "//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info']";
      if (selenium.isElementPresent(msgLocator))
      {
         assertEquals("", selenium.getText(msgLocator));
      }
   }
   
   void checkCreateFileFromTemplateFormAndClose() throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      Thread.sleep(TestConstants.SLEEP);
   }
   
   void checkCustomizeHotkeyDialogWindow()
   {
      assertTrue(selenium.isElementPresent(CUSTOMIZE_HOTKEYS_FORM_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]"));
      checkTextFieldEnabled(false);
      checkBindButtonEnabled(false);
      checkUnbindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      checkCancelButtonEnabled(true);
   }
   
   void checkNoCustomizeHotkeyDialogWindow()
   {
      assertFalse(selenium.isElementPresent(CUSTOMIZE_HOTKEYS_FORM_LOCATOR));
   }
   
   void checkBindButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertFalse(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
      }
   }
   
   void checkUnbindButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Unbind']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Unbind']"));
      }
   }
   
   void checkSaveButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Save']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Save']"));
      }
   }
   
   void checkCancelButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Cancel']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Cancel']"));
      }
   }
   
   void checkTextFieldEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertFalse(selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItemDisabled']"));
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItemFocused']")
            || selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItem']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItem']"));
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItemDisabled']"));
      }
   }
   
   /**
    * Check is message present.
    * 
    * If message is present, than check text
    * 
    * @param style INFO or ERROR message is shown (INFO - in blue color, ERROR - in red color)
    * @param message - text message
    * @param isPresent is message present
    */
   void checkMessage(String style, String message, boolean isPresent)
   {
      //check is no message present
      if (!isPresent)
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertFalse(selenium
               .isElementPresent("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info' and text()='" + message + "']"));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertFalse(selenium
               .isElementPresent("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error' and text()='" + message + "']"));
         }
      }
      //check displayed message
      else
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertEquals(message, selenium
               .getText("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info']"));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertEquals(message, selenium
               .getText("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error']"));
         }
      }
   }
   
   void closeHotkeysWindow() throws Exception
   {
      selenium.click("scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/closeButton/");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   String getTextFromTextField()
   {
      return selenium.getValue("scLocator=//DynamicForm[ID=\"ideCustomizeHotKeysFormDynamicFormHotKeyField\"]/item[0]" 
         + "[name=\"ideCustomizeHotKeysFormHotKeyField\"]/element");
   }
   
   /**
    * Get text from Bind column in list grid by row title.
    * 
    * @param rowTitle text in row in Command column.
    * 
    * @return {@link String}
    * @throws Exception 
    */
   String getTextFromBindColumn(String rowTitle) throws Exception
   {
      String rowLocator = getRowLocator(rowTitle);
      
      if (!selenium.isElementPresent(rowLocator))
      {
         fail("Can't find row locator: " + rowLocator);
      }
      String bindLocator = rowLocator.replace("col[0]", "col[1]");
      
      return selenium.getText(bindLocator);
   }

}
