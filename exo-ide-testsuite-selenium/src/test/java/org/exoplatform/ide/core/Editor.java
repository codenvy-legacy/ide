/**
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
 *
 */

package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.thoughtworks.selenium.Selenium;

import org.exoplatform.ide.Locators;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;

import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Editor
{

   private Selenium selenium;

   public Editor(Selenium selenium)
   {
      this.selenium = selenium;
   }

   /**
    * Returns the title of the tab with the pointed index.
    * 
    * @param index tab index
    * @return {@link String} tab's title
    * @throws Exception
    */
   public String getTabTitle(int index)
   {
      return selenium.getText(getEditorTabScLocator(index) + "/title");
   }

   /**
    * Get smart GWT locator for editor tab.
    * 
    * @param tabIndex - index of tab
    * @return {@link String}
    */
   public String getEditorTabScLocator(int tabIndex)
   {
      return Locators.SC_EDITOR_TABSET_LOCATOR + "/tab[index=" + tabIndex + "]";
   }

   /**
    * Click on editor tab to make it active.
    * 
    * Numbering of tabs starts with 0.
    * 
    * @param tabIndex index of tab
    * @throws Exception
    */
   public void selectTab(int tabIndex) throws Exception
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + String.valueOf(tabIndex) + "]/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Close tab by it's index.
    * 
    * @param index numeration starts with 0 index
    */
   public void closeTab(int index) throws Exception
   {
      selenium.mouseOver(Locators.getTabCloseButtonLocator(index));
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.click(Locators.getTabCloseButtonLocator(index));
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * In case tab with index as tabIndex is exist in editor, close it without saving. 
    * 
   * @param tabIndex
   */
   public void tryCloseTabWithNonSaving(int tabIndex) throws Exception
   {
      //if file is opened, close it
      if (selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + tabIndex + "]/icon"))
      {
         closeTabWithNonSaving(tabIndex);
      }
   }

   /**
    * Close tab in editor. Close ask window in case it appear while closing.
    * 
   * @param tabIndex
   * @throws Exception
   */
   public void closeTabWithNonSaving(int tabIndex) throws Exception
   {
      closeTab(tabIndex);

      //check is warning dialog appears
      if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
      {
         //click No button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.SLEEP);
      }
   }

   /**
    * Close unsaved file without saving it.
    * 
    * Close tab with tabIndex. Check is warning dialog appears.
    * Click No (Discard) button if file is new.
    * 
    * @param tabIndex index of tab to close
    * @throws Exception
    */
   public void closeUnsavedFileAndDoNotSave(int tabIndex) throws Exception
   {
      //check, that file is unsaved
      final String tabName = getTabTitle(Integer.valueOf(tabIndex));
      assertTrue(tabName.endsWith("*"));
      closeTab(tabIndex);

      /*
       * close existed file
       * SmartGWT not destroy warning dialog(only hide, maybe set smoller z-index property ),
       * so need check is warning dialogs is visible
       */
      if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]")
         && selenium.isVisible("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
      {
         //check is warning dialog appears
         assertTrue(selenium
            .isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));

         assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
         assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));

         //click No button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      }
      //close new file
      else if (selenium.isElementPresent(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR)
         && selenium.isVisible(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR))
      {
         selenium.click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
      }
      else
      {
         fail("Unknown warning dialog!");
      }
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Close file tab, and don't see into file has changes or no.
    * 
    * If tab's title doesn't ends with *, simple close tab.
    * Otherwise, waits for warning dialog window and click No button.
    * 
    * @param tabIndex - index of tab with file to close
    * @throws Exception
    */
   public void closeFileTabIgnoreChanges(int tabIndex) throws Exception
   {
      //check, is file was changed
      final String tabName = getTabTitle(Integer.valueOf(tabIndex));

      if (tabName.endsWith("*"))
      {
         closeUnsavedFileAndDoNotSave(tabIndex);
      }
      else
      {
         closeTab(tabIndex);
      }
   }

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
   public void closeNewFile(int tabIndex, boolean saveFile, String fileName) throws Exception
   {
      closeTab(tabIndex);

      if (saveFile)
      {
         SaveFileUtils.checkSaveAsDialogAndSave(fileName, true);
         //          if(fileName != null)
         //          {
         //             AbstractTextUtil.getInstance().typeToInput(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR, fileName, true);
         //          }
         //          selenium.click(ASK_FOR_VALUE_OK_BUTTON_LOCATOR);
      }
      else
      {
         SaveFileUtils.checkSaveAsDialog(true);
         selenium.click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
      }
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

   }

   public void checkEditorTabSelected(String tabTitle, boolean isSelected)
   {
      if (isSelected)
      {
         //used //td[contains(@class, 'tabTitleSelected')] locator, instead of equals,
         //because after refreshing tab is overed by mouse and there is no 'tabTitleSelected'
         //class, but there is 'tabTitleSelectedOver'.
         assertTrue(selenium.isElementPresent(Locators.EDITOR_PANEL_LOCATOR
            + "//div[@class='tabBar']//td[contains(@class, 'tabTitleSelected')]/span[contains(text(), '" + tabTitle
            + "')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(Locators.EDITOR_PANEL_LOCATOR
            + "//div[@class='tabBar']//td[@class='tabTitle']/span[contains(text(), '" + tabTitle + "')]"));
      }
   }

   /**
    * Because of troubles of finding element, which contains text with symbol *,
    * this method doesn't check directly is editor tab locator with <code>tabTitle</code> present.
    * <p>
    * This method checks editor tabs with indexes from 0 to 50 (I think there will not be opened more)
    * and checks: if tab with such index present, compares its title with <code>tabTitle</code>.
    * 
    * @param tabTitle - title of editor tab
    * @param isOpened - is tab must be opened
    * @throws Exception
    */
   public void checkIsTabPresentInEditorTabset(String tabTitle, boolean isOpened)
   {
      if (isOpened)
      {
         for (int i = 0; i < 50; i++)
         {
            if (selenium.isElementPresent(getEditorTabScLocator(i)))
            {
               if (tabTitle.equals(getTabTitle(i)))
                  return;
            }
            else
            {
               break;
            }
         }
         fail("Can't find " + tabTitle + "in tab titles");
      }
      else
      {
         for (int i = 0; i < 50; i++)
         {
            if (selenium.isElementPresent(getEditorTabScLocator(i)))
            {
               if (tabTitle.equals(getTabTitle(i)))
                  fail(tabTitle + "is present in tab titles");
            }
            else
            {
               return;
            }
         }
      }
   }

   /**
    * Delete pointed number of lines in editor.s
    * 
    * @param count number of lines to delete
    */
   public void deleteLinesInEditor(int count)
   {
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      for (int i = 0; i < count; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_D);
      }
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
   }

   /**
    *  Delete all file content via Ctrl+a, Delete
    */
   public void deleteFileContent() throws Exception
   {
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);

      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);

      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Clicks on editor panel with the help of {@link Robot}.
    * It makes a system click, so the coordinates, where to click are computered, 
    * taking into consideration the browser outer and inner height.
    * 
    * @param index editor tab's index
    * @throws Exception
    */
   public void clickOnEditor() throws Exception
   {
      // Make system mouse click on editor space
      Robot robot = new Robot();
      robot.mouseMove(getEditorLeftScreenPosition() + 50, getEditorTopScreenPosition() + 50);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      robot.mousePress(InputEvent.BUTTON1_MASK);
      robot.mouseRelease(InputEvent.BUTTON1_MASK);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      //Second click is needed in some tests with outline , because editor looses focus:
      robot.mousePress(InputEvent.BUTTON1_MASK);
      robot.mouseRelease(InputEvent.BUTTON1_MASK);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      // Put cursor at the beginning of the document
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_UP);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Returns the editor's left position on the screen
    * 
    * @return int x
    */
   protected int getEditorLeftScreenPosition()
   {
      // Get the delta between of toolbar browser area
      int deltaX = Integer.parseInt(selenium.getEval("window.outerWidth-window.innerWidth"));
      // Get the position on screen of the editor
      int x = selenium.getElementPositionLeft("//div[@class='tabSetContainer']/div/div[2]//iframe").intValue() + deltaX;
      return x;
   }

   /**
    * Returns the editor's top position on the screen
    * 
    * @return int y
    */
   protected int getEditorTopScreenPosition()
   {
      // Get the delta between of toolbar browser area
      int deltaY = Integer.parseInt(selenium.getEval("window.outerHeight-window.innerHeight"));
      // Get the position on screen of the editor
      int y = selenium.getElementPositionTop("//div[@class='tabSetContainer']/div/div[2]//iframe").intValue() + deltaY;
      return y;
   }

}
