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
import org.exoplatform.ide.core.Templates;
import org.junit.After;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public abstract class AbstractHotkeysTest extends BaseTest
{
   static final String MESSAGE_LABEL = "ideCustomizeHotKeysMessageLabel";

   static final int NUMBER_OF_COMMANDS = 500;

   static final String GOOGLE_GADGET_FILE = "GoogleGadget.xml";

   static final String DEFAULT_TEXT_IN_GADGET = "Hello, world!";

   static String FOLDER_NAME;

   static final String INFO_MESSAGE_STYLE = "exo-cutomizeHotKey-label-info";

   static final String ERROR_MESSAGE_STYLE = "exo-cutomizeHotKey-label-error";

   static final String BIND_BUTTON_LOCATOR = "ideCustomizeHotKeysViewBindButton";

   static final String SAVE_BUTTON_LOCATOR = "ideCustomizeHotKeysViewOkButton";

   static final String CUSTOMIZE_HOTKEYS_FORM_LOCATOR = "ideCustomizeHotKeysView-window";

   static final String UNBIND_BUTTON_LOCATOR = "ideCustomizeHotKeysViewUnbindButton";

   static final String CANCEL_BUTTON_LOCATOR = "ideCustomizeHotKeysViewCancelButton";

   static final String TEXT_FIELD_LOCATOR = "ideCustomizeHotKeysViewHotKeyField";

   static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   String locator;

   interface Commands
   {
      public static final String CREATE_FILE_FROM_TEMPLATE = "Create File From Template...";

      public static final String NEW_CSS_FILE = "New CSS";

      public static final String NEW_TEXT_FILE = "New TEXT";

      public static final String NEW_HTML_FILE = "New HTML";
   }

   @After
   public void tearDown()
   {
      deleteCookies();
   }

   void selectRow(String rowTitle) throws Exception
   {
      String locator = getRowLocator(rowTitle);
      if (locator == null)
      {
         fail("Can't find locator " + locator + " in list");
      }
      selenium().click(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   void deselectRow(String rowTitle) throws Exception
   {
      locator = getRowLocator(Commands.NEW_CSS_FILE);
      if (locator == null)
      {
         fail("Can't find locator " + locator + " in list");
      }
      selenium().controlKeyDown();
      selenium().click(locator);
      selenium().controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   String getRowLocator(String name) throws Exception
   {
      String locator = null;

      for (int rowNumber = 1; rowNumber <= NUMBER_OF_COMMANDS; rowNumber++)
      {
         locator = "//table[@id='ideCustomizeHotKeysListGrid']/tbody/tr[" + rowNumber + "]/td";
         System.out.println(locator);
         if (selenium().isElementPresent(locator))
         {
            String text = selenium().getText(locator);
            System.out.println(text);
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


   void clickButton(String buttonLocator) throws Exception
   {
      selenium().click(buttonLocator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   void checkNoMessage() throws Exception
   {
      String msgLocator = "//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error']";
      if (selenium().isElementPresent(msgLocator))
      {
         assertEquals("", selenium().getText(msgLocator));
      }
      msgLocator = "//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info']";
      if (selenium().isElementPresent(msgLocator))
      {
         assertEquals("", selenium().getText(msgLocator));
      }
   }

   void checkCreateFileFromTemplateFormAndClose() throws Exception
   {
      assertTrue(IDE.TEMPLATES.isOpened());
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
   }

   void checkCustomizeHotkeyDialogWindow()
   {
      assertTrue(selenium().isElementPresent(CUSTOMIZE_HOTKEYS_FORM_LOCATOR));
      assertTrue(selenium().isElementPresent("ideCustomizeHotKeysListGrid"));
      checkTextFieldEnabled(false);
      checkBindButtonEnabled(false);
      checkUnbindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      checkCancelButtonEnabled(true);
   }

   void checkNoCustomizeHotkeyDialogWindow()
   {
      assertFalse(selenium().isElementPresent(CUSTOMIZE_HOTKEYS_FORM_LOCATOR));
   }

   void checkBindButtonEnabled(boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='ideCustomizeHotKeysViewBindButton' and @button-enabled='"
         + isEnabled + "']"));
   }

   void checkUnbindButtonEnabled(boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='ideCustomizeHotKeysViewUnbindButton' and @button-enabled='"
         + isEnabled + "']"));
   }

   void checkSaveButtonEnabled(boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='ideCustomizeHotKeysViewOkButton' and @button-enabled='"
         + isEnabled + "']"));
   }

   void checkCancelButtonEnabled(boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='ideCustomizeHotKeysViewCancelButton' and @button-enabled='"
         + isEnabled + "']"));
   }

   void checkTextFieldEnabled(boolean isEnabled)
   {

      if (isEnabled)
      {
         assertTrue(selenium().isEditable(TEXT_FIELD_LOCATOR));
      }
      else
      {
         assertFalse(selenium().isEditable(TEXT_FIELD_LOCATOR));
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
      String messageId = MESSAGE_LABEL;
      if (!isPresent)
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertFalse(selenium()
               .isElementPresent("//div[@id='" + messageId + "' and text()='" + message + "']"));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertFalse(selenium().isElementPresent("//div[@id='" + messageId + "' and text()='" + message + "']"));
         }
      }
      //check displayed message
      else
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertEquals(message,
               selenium().getText(messageId));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertEquals(message,
               selenium().getText(messageId));
         }
      }
   }

   void closeHotkeysWindow() throws Exception
   {
      selenium().click(CANCEL_BUTTON_LOCATOR);
      waitForElementNotPresent(CUSTOMIZE_HOTKEYS_FORM_LOCATOR);
   }

   String getTextFromTextField()
   {
      return selenium().getValue(TEXT_FIELD_LOCATOR);
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

      if (!selenium().isElementPresent(rowLocator))
      {
         fail("Can't find row locator: " + rowLocator);
      }
      String bindLocator = rowLocator + "[2]";//.replace("col[0]", "col[1]");

      return selenium().getText(bindLocator);
   }

}
