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
package org.exoplatform.ide;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.utils.AbstractTextUtil;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CloseFileUtils
{
   static Selenium selenium;

   private static final String ASK_FOR_VALUE_DIALOG_LOCATOR = "scLocator=//Window[ID=\"ideAskForValueDialog\"]";

   private static final String ASK_FOR_VALUE_OK_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/";

   private static final String ASK_FOR_VALUE_NO_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogNoButton\"]/";

   private static final String ASK_FOR_VALUE_CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/";

   private static final String ASK_FOR_VALUE_TEXT_FIELD_LOCATOR =
      "scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/"
         + "item[name=ideAskForValueDialogValueField]/element";
   
   /**
    * Close new file. 
    * If saveFile true - save file.
    * If fileName is null - save with default name, else
    * save with fileName name.
    * 
    * @param tabIndex - index of tab in editor panel
    * @param saveFile - is save file before closing
    * @param fileName - name of new file
    * @throws Exception
    */
   public static void closeNewFile(int tabIndex, boolean saveFile, String fileName) throws Exception
   {
      CloseFileUtils.closeTab(tabIndex);

      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_DIALOG_LOCATOR));
      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_OK_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_NO_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_CANCEL_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR));

      if (saveFile)
      {
         if(fileName != null)
         {
            AbstractTextUtil.getInstance().typeToInput(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR, fileName, true);
         }
         selenium.click(ASK_FOR_VALUE_OK_BUTTON_LOCATOR);
      }
      else
      {
         selenium.click(ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
      }
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

   }
   
   /**
    * Close tab by it's index.
    * 
    * @param index numeration starts with 0 index
    */
   public static void closeTab(int index) throws Exception
   {
      selenium.mouseOver(Locators.getTabCloseButtonLocator(index));
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.click(Locators.getTabCloseButtonLocator(index));
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
}
