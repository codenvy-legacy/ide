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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.awt.Robot;
import java.awt.event.InputEvent;

import org.exoplatform.ide.Locators;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.utils.AbstractTextUtil;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Editor extends AbstractTestModule
{

   public interface EditorLocators
   {
      /**
       * XPATH CK editor locator.
       */
      public static final String CK_EDITOR = "//table[@class='cke_editor']";
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
      return selenium().getText(getEditorTabScLocator(index));
   }

   /**
    * Get smart GWT locator for editor tab.
    * 
    * @param tabIndex - index of tab
    * @return {@link String}
    */
   public String getEditorTabScLocator(int tabIndex)
   {
      return Locators.EDITOR_TABSET_LOCATOR + "//td[@tab-bar-index='" + tabIndex + "']";
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
      selenium().clickAt("//div[@panel-id='editor']//td[@tab-bar-index=" + String.valueOf(tabIndex) + "]" + "/table",
         "");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Close tab by it's index.
    * 
    * @param index numeration starts with 0 index
    */
   public void closeTab(int index) throws Exception
   {
      String tabLocator = Locators.getTabCloseButtonLocator(index);
      if (selenium().isElementPresent(tabLocator))
      {
         selenium().mouseOver(tabLocator);
         Thread.sleep(TestConstants.ANIMATION_PERIOD);

         selenium().click(tabLocator);
         Thread.sleep(TestConstants.REDRAW_PERIOD);
      }
   }

   /**
    * In case tab with index as tabIndex is exist in editor, close it without saving. 
    * 
   * @param tabIndex
   */
   public void tryCloseTabWithNonSaving(int tabIndex) throws Exception
   {
      //if file is opened, close it
      if (selenium().isElementPresent("//div[@panel-id='editor']//td[@tab-bar-index='" + tabIndex + "']"))
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
      if (IDE().ASK_DIALOG.isDialogOpened("Close file"))
      {
         IDE().ASK_DIALOG.clickNo();
      }
      else if (selenium().isElementPresent(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR))
      {
         selenium().click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
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
      if (selenium().isElementPresent("exoAskDialog") && selenium().isVisible("exoAskDialog"))
      {
         //check is warning dialog appears
         assertTrue(selenium().isElementPresent(
            "//div[@id='exoAskDialog']//div[@class='Caption']/span[contains(text(), 'Close file')]"));

         assertTrue(selenium().isElementPresent("exoAskDialogYesButton"));
         assertTrue(selenium().isElementPresent("exoAskDialogNoButton"));

         //click No button
         selenium().click("exoAskDialogNoButton");
      }
      //close new file
      else if (selenium().isElementPresent(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR)
         && selenium().isVisible(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR))
      {
         selenium().click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
      }
      else
      {
         fail("Unknown warning dialog!");
      }
      Thread.sleep(TestConstants.REDRAW_PERIOD);
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
      }
      else
      {
         SaveFileUtils.checkSaveAsDialog(true);
         selenium().click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
      }

      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   public void checkEditorTabSelected(String tabTitle, boolean isSelected)
   {
      String locator = Locators.EDITOR_TABSET_LOCATOR;
      if (isSelected)
      {
         //used //td[contains(@class, 'tabTitleSelected')] locator, instead of equals,
         //because after refreshing tab is overed by mouse and there is no 'tabTitleSelected'
         //class, but there is 'tabTitleSelectedOver'.
         locator += "//td[contains(@class, 'gwt-TabBarItem-wrapper-selected')]//span[contains(text(), '" + tabTitle + "')]";
      }
      else
      {
         locator += "//div[@role='tab']//span[contains(text(), '" + tabTitle + "')]";
      }

      System.out.println("locator [" + locator + "]");
      
      assertTrue(selenium().isElementPresent(locator));
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
            if (selenium().isElementPresent(getEditorTabScLocator(i)))
            {
               if (tabTitle.equals(getTabTitle(i)))
                  return;
            }
            else
            {
               break;
            }
         }
         fail("Can't find " + tabTitle + " in tab titles");
      }
      else
      {
         for (int i = 0; i < 50; i++)
         {
            if (selenium().isElementPresent(getEditorTabScLocator(i)))
            {
               if (tabTitle.equals(getTabTitle(i)))
                  fail(tabTitle + " is present in tab titles");
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
      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      for (int i = 0; i < count; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_D);
      }
      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
   }

   /**
    *  Delete all file content via Ctrl+a, Delete
    */
   public void deleteFileContent() throws Exception
   {
      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_A);

      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);

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
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_UP);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);
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
      int deltaX = Integer.parseInt(selenium().getEval("window.outerWidth-window.innerWidth"));
      // Get the position on screen of the editor
      //      int x = selenium.getElementPositionLeft("//div[@class='tabSetContainer']/div/div[2]//iframe").intValue() + deltaX;
      int x =
         selenium().getElementPositionLeft("//div[@panel-id='editor' and @tab-index='0' ]//iframe").intValue() + deltaX;
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
      int deltaY = Integer.parseInt(selenium().getEval("window.outerHeight-window.innerHeight"));
      // Get the position on screen of the editor
      int y =
         selenium().getElementPositionTop("//div[@panel-id='editor' and @tab-index='0']//iframe").intValue() + deltaY;
      return y;
   }

   /**
    * Press Enter key in editor.
    */
   public void pressEnter()
   {
      selenium().keyDown("//body[@class='editbox']", "\\13");
   }

   /**
    * Type text to file, opened in tab.
    * 
    * Index of tabs begins from 0.
    * 
    * Sometimes, if you can't type text to editor,
    * try before to click on editor:
    * 
    * selenium.clickAt("//body[@class='editbox']", "5,5");
    * 
    * @param tabIndex begins from 0
    * @param text (can be used '\n' as line break)
    */
   public void typeTextIntoEditor(int tabIndex, String text) throws Exception
   {
      if (selenium().isElementPresent(
         getContentPanelLocator(tabIndex) + "//table[@class='cke_editor']//td[@class='cke_contents']/iframe"))
      {
         selectIFrameWithEditor(tabIndex);
         AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CK_EDITOR_LOCATOR, text);
      }
      else
      {
         selectIFrameWithEditor(tabIndex);
         AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, text);
      }

      IDE().selectMainFrame();
   }

   /**
    * 
    * @param tabIndex begins from 0
    * @return content panel locator 
    */
   public String getContentPanelLocator(int tabIndex)
   {
      return "//div[@panel-id='editor' and @tab-index='" + tabIndex + "' ]";

      //      //      String divIndex = String.valueOf(tabIndex + 2);
      //      if (BROWSER_COMMAND.equals(EnumBrowserCommand.IE_EXPLORE_PROXY))
      //      {
      //         return "//div[@class='tabSetContainer']/div[" + tabIndex + "]";
      //      }
      //      else
      //      {
      //         return "//div[@panel-id='editor' and @tab-index='" + tabIndex + "' ]";
      //         //         return "//div[@class='tabSetContainer']/div/div[" + divIndex + "]";
      //      }
   }

   /**
    * Select iframe, which contains editor from tab with index tabIndex
    * 
    * @param tabIndex begins from 0
    */
   public void selectIFrameWithEditor(int tabIndex) throws Exception
   {
      selenium().selectFrame(getContentPanelLocator(tabIndex) + "//iframe");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Mouse click on editor.
    * 
    * @param tabIndex - tab index.
    * @throws Exception
    */
   public void clickOnEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      selenium().clickAt("//body[@class='editbox']", "5,5");
      IDE().selectMainFrame();
   }

   /**
    * Get text from tab number "tabIndex" from editor
    * @param tabIndex begins from 0
    */
   public String getTextFromCodeEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      String text = selenium().getText("//body[@class='editbox']");
      IDE().selectMainFrame();
      return text;
   }

   public String getTextFromCKEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      String text = selenium().getText("//body");
      IDE().selectMainFrame();
      return text;
   }

   /**
    * Run hot key within editor. 
    * 
    * This method used for running hotkeys for editor, such as ctrl+z, ctrl+a, ctrl+s and so on.
    * 
    * @param tabIndex index of tab
    * @param isCtrl is control key used
    * @param isAlt is alt key used
    * @param keyCode virtual code of key (code of key on keyboard)
    */
   public void runHotkeyWithinEditor(int tabIndex, boolean isCtrl, boolean isAlt, int keyCode) throws Exception
   {
      selectIFrameWithEditor(tabIndex);

      if (isCtrl)
      {
         selenium().controlKeyDown();
      }
      if (isAlt)
      {
         selenium().altKeyDown();
      }

      selenium().keyDown("//", String.valueOf(keyCode));
      selenium().keyUp("//", String.valueOf(keyCode));

      if (isCtrl)
      {
         selenium().controlKeyUp();
      }
      if (isAlt)
      {
         selenium().altKeyUp();
      }

      IDE().selectMainFrame();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
   * wait present tab editor element 
   * @param tabIndex - index of tab, starts at 0
   * @throws Exception
   */
   public void waitTabPresent(int tabIndex) throws Exception
   {
      waitForElementPresent("//div[@panel-id='editor']//td[@tab-bar-index=" + String.valueOf(tabIndex) + "]" + "/table");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }
   
   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex index of tab, starts at 0
    * @throws Exception
    */
   public void checkCkEditorOpened(int tabIndex) throws Exception
   {
      String locator =
         "//div[@panel-id='editor' and @tab-index='" + tabIndex
            + "']//table[@class='cke_editor']//td[@class='cke_contents']/iframe";
      assertTrue(selenium().isElementPresent(locator));
   }

   /**
    * Check is file in tabIndex tab opened with Code Editor.
    * 
    * @param tabIndex
    * @throws Exception
    */
   public void checkCodeEditorOpened(int tabIndex) throws Exception
   {
      String locator =
         "//div[@panel-id='editor'and @tab-index='" + tabIndex + "']//div[@class='CodeMirror-wrapping']/iframe";
      assertTrue(selenium().isElementPresent(locator));
   }
   
   /**
    * Check is line numbers are shown in editor
    * 
    * @param visible is line numbers must be shown
    */
   public void checkLineNumbersVisible(boolean visible)
   {
      if (visible)
      {
         assertTrue(selenium().isElementPresent("//div[@class='CodeMirror-line-numbers']"));
      }
      else
      {
         assertFalse(selenium().isElementPresent("//div[@class='CodeMirror-line-numbers']"));
      }
   }

}
