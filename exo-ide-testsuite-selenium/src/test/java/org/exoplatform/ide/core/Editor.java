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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public class Editor extends AbstractTestModule
{

   public interface Locators
   {
      /**
       * XPATH CodeMirror locator. 
       */
      public static final String CODE_MIRROR_EDITOR = "//body[@class='editbox']";

      /**
       * XPATH CK editor locator.
       */
      String CK_EDITOR = "//table[@class='cke_editor']";

      String EDITOR_TABSET_LOCATOR = "//div[@id='editor']";

      String TAB_LOCATOR = "//div[@tab-bar-index='%s']";

      String EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]";

      String EDITOR_VIEW_LOCATOR = "//div[@panel-id='editor' and @view-id='%s']";

      String ACTIVE_EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and is-active='true']";

      String SELECTED_EDITOR_TAB_LOCATOR =
         "//div[contains(@class, 'gwt-TabLayoutPanelTab-selected') and contains(text(), '%s')]";

      String DEBUG_EDITOR_ACTIVE_FILE_URL = "debug-editor-active-file-url";

      String DEBUG_EDITOR_PREVIOUS_ACTIVE_FILE_URL = "debug-editor-previous-active-file-url";

      String DESIGN_BUTTON_LOCATOR = "//div[@id='DesignButtonID']";

      String SOURCE_BUTTON_LOCATOR = "//div[@id='SourceButtonID']/span";

      String CLOSE_BUTTON_LOCATOR = "//div[@button-name='close-tab']";
   }

   private WebElement editor;

   /**
    * Returns the title of the tab with the pointed index.
    * 
    * @param index tab index
    * @return {@link String} tab's title
    * @throws Exception
    */
   public String getTabTitle(int index)
   {
      WebElement tab =
         editor.findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_LOCATOR, index)));
      return tab.getText();
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
      WebElement tab =
         editor.findElement(By.xpath(String.format(Locators.EDITOR_TABSET_LOCATOR + Locators.TAB_LOCATOR, tabIndex)));
      tab.click();
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Click on Close Tab button.
    * Old name of this method is "clickCloseTabButton(int tabIndex)"
    * 
    * @param tabIndex index of tab, starts at 0
    */
   public void clickCloseEditorButton(int tabIndex) throws Exception
   {
      WebElement closeButton =
         editor.findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_LOCATOR, tabIndex)
            + Locators.CLOSE_BUTTON_LOCATOR));
      closeButton.click();
   }

   /**
    * Closes file 
    * 
    * @param tabIndex
    */
   public void closeFile(int tabIndex) throws Exception
   {
      selectTab(tabIndex);
      final String viewId = editor.findElement(By.xpath(Locators.ACTIVE_EDITOR_TAB_LOCATOR)).getAttribute("view-id");
      clickCloseEditorButton(tabIndex);

      new WebDriverWait(driver(), 2000).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return input.findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR, viewId))) == null;
         }
      });
   }

   /**
    * Close tab in editor. Close ask window in case it appear while closing.
    * 
   * @param tabIndex index of tab, starts at 0
   * @throws Exception
   */
   public void closeTabIgnoringChanges(int tabIndex) throws Exception
   {
      selectTab(tabIndex);
      final String viewId = editor.findElement(By.xpath(Locators.ACTIVE_EDITOR_TAB_LOCATOR)).getAttribute("view-id");
      clickCloseEditorButton(tabIndex);

      /*
       * Closing ask dialogs if them is appears.
       */
      if (IDE().ASK_DIALOG.isOpened())
      {
         IDE().ASK_DIALOG.clickNo();
      }
      else if (IDE().ASK_FOR_VALUE_DIALOG.isOpened())
      {
         IDE().ASK_FOR_VALUE_DIALOG.clickNoButton();
      }
      else
      {
         fail("Dialog has been not found!");
      }

      new WebDriverWait(driver(), 2000).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return input.findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR, viewId))) == null;
         }
      });
   }

   /**
    * 
    * 
    * @param tabIndex index of tab, starts at 0
    * @return
    */
   public boolean isFileContentChanged(int tabIndex)
   {
      //check, that file is unsaved
      final String tabName = getTabTitle(tabIndex);
      return tabName.endsWith("*");
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
   public void saveAndCloseFile(int tabIndex, String newFileName) throws Exception
   {
      selectTab(tabIndex);
      final String viewId = editor.findElement(By.xpath(Locators.ACTIVE_EDITOR_TAB_LOCATOR)).getAttribute("view-id");

      clickCloseEditorButton(tabIndex);

      /*
       * Saving file
       */
      if (IDE().ASK_DIALOG.isOpened())
      {
         IDE().ASK_DIALOG.clickYes();
      }
      else if (IDE().ASK_FOR_VALUE_DIALOG.isOpened())
      {
         if (newFileName != null && !newFileName.isEmpty())
         {
            IDE().ASK_FOR_VALUE_DIALOG.setValue(newFileName);
         }
         IDE().ASK_FOR_VALUE_DIALOG.clickOkButton();
      }
      else
      {
         fail();
      }

      new WebDriverWait(driver(), 2000).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR, viewId)));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public boolean isEditorTabSelected(String tabTitle)
   {
      return editor.findElement(By.xpath(String.format(Locators.SELECTED_EDITOR_TAB_LOCATOR, tabTitle))) != null;
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
      /*  if (isOpened)
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
        }*/
      //TODO
   }

   /**
    * Determines whether the file with specified URL is opened in Editor.
    * 
    * @param fileURL
    * @return
    */
   public boolean isFileOpened(String fileURL)
   {
      String locator =
         "//div[@panel-id='editor' and @is-panel='true']//table[@id='editor-panel-switcher']//table[@class='gwt-DecoratedTabBar']//"
            + "span[@title='" + fileURL + "']";
      return selenium().isElementPresent(locator);
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
      String command = Keys.CONTROL + "a" + Keys.DELETE;
      typeTextIntoEditor(0, command);

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
      //      int x = selenium().getElementPositionLeft("//div[@class='tabSetContainer']/div/div[2]//iframe").intValue() + deltaX;
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
    * Type text to file, opened in tab.
    * 
    * Index of tabs begins from 0.
    * 
    * Sometimes, if you can't type text to editor,
    * try before to click on editor:
    * 
    * selenium().clickAt("//body[@class='editbox']", "5,5");
    * 
    * @param tabIndex begins from 0
    * @param text (can be used '\n' as line break)
    */
   public void typeTextIntoEditor(int tabIndex, String text) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      driver().switchTo().activeElement().sendKeys(text);
      IDE().selectMainFrame();
   }

   /**
    * Move cursor in editor down to pointed number of lines.
    * 
    * @param tabIndex index of the tab
    * @param rows number of lines to move down
    * @throws Exception 
    */
   public void moveCursorDown(int tabIndex, int rows) throws Exception
   {
      for (int i = 0; i < rows; i++)
      {
         typeTextIntoEditor(tabIndex, Keys.DOWN.toString());
      }
   }

   /**
    * Move cursor in editor up to pointed number of lines.
    * 
    * @param tabIndex index of the tab
    * @param rows number of lines to move up
    * @throws Exception 
    */
   public void moveCursorUp(int tabIndex, int rows) throws Exception
   {
      for (int i = 0; i < rows; i++)
      {
         typeTextIntoEditor(tabIndex, Keys.UP.toString());
      }
   }

   /**
    * Move cursor in editor left to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move left
    * @throws Exception 
    */
   public void moveCursorLeft(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoEditor(tabIndex, Keys.LEFT.toString());
      }
   }

   /**
    * Move cursor in editor right to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move right
    * @throws Exception 
    */
   public void moveCursorRight(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoEditor(tabIndex, Keys.RIGHT.toString());
      }
   }

   /**
    * Get the locator of content panel.
    * 
    * 
    * @param tabIndex starts from 0
    * @return content panel locator 
    */
   public String getContentPanelLocator(int tabIndex)
   {
      return String.format(Locators.EDITOR_TAB_LOCATOR, tabIndex);
   }

   /**
    * Select iframe, which contains editor from tab with index tabIndex
    * 
    * @param tabIndex begins from 0
    */
   public void selectIFrameWithEditor(int tabIndex) throws Exception
   {
      String iFrameWithEditorLocator = getContentPanelLocator(tabIndex) + "//iframe";
      WebElement editorFrame = driver().findElement(By.xpath(iFrameWithEditorLocator));
      driver().switchTo().frame(editorFrame);
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
    * Wait while tab appears in editor 
    * @param tabIndex - index of tab, starts at 0
    * @throws Exception
    */
   public void waitTabPresent(int tabIndex) throws Exception
   {
      waitForElementPresent("//div[@panel-id='editor']//td[@tab-bar-index=" + String.valueOf(tabIndex) + "]" + "/table");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Wait while tab disappears in editor 
    * @param tabIndex - index of tab, starts at 0
    * @throws Exception
    */
   public void waitTabNotPresent(int tabIndex) throws Exception
   {
      waitForElementNotPresent("//div[@panel-id='editor']//td[@tab-bar-index=" + String.valueOf(tabIndex) + "]"
         + "/table");
   }

   public void waitEditorFileOpened() throws Exception
   {
      /*
       * click for element to clear it's text
       */
      //webdriver don't alloy click to invisible elements!!! 
      //      selenium().click("debug-editor-active-file-url");

      //      Thread.sleep(1);
      waitForElementTextIsNotEmpty("debug-editor-active-file-url");
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
      //assertTrue(selenium().isVisible(locator));
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
      assertTrue(selenium().isVisible(locator));
   }

   /**
    * Determines whether specified tab opened in Editor.
    * 
    * @param tabIndex index of tab, starts at 0
    * @return
    */
   public boolean isTabOpened(int tabIndex)
   {
      //      String locator = "//div[@panel-id='editor' and @is-panel='true']//tabe[@id='editor-panel-switcher']" +
      //      		"//table[@class='gwt-DecoratedTabBar']/tbody/tr/td/[@tab-bar-index='" + tabIndex +  "']";
      String locator =
         "//div[@panel-id='editor' and @is-panel='true']//table[@id='editor-panel-switcher']//table[@class='gwt-DecoratedTabBar']/tbody/tr/td[@tab-bar-index='0']";
      return selenium().isElementPresent(locator);
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

   /**
    * Click on Source button at the bottom of editor.
    * 
    * @throws Exception
    */
   public void clickSourceButton() throws Exception
   {
      assertTrue("Button 'Source' is absent!", selenium().isElementPresent(Locators.SOURCE_BUTTON_LOCATOR));
      selenium().keyPress(Locators.SOURCE_BUTTON_LOCATOR, "\\13"); // hack to simulate click as described for GWT ToogleButton in the http://code.google.com/p/selenium/issues/detail?id=542
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Click on Design button at the bottom of editor.
    * 
    * @throws Exception
    */
   public void clickDesignButton() throws Exception
   {
      assertTrue("Button 'Design' is absent!", selenium().isElementPresent(Locators.DESIGN_BUTTON_LOCATOR));
      selenium().keyPress(Locators.DESIGN_BUTTON_LOCATOR, "\\13");
      Thread.sleep(TestConstants.SLEEP);
   }

   public void selectCkEditorIframe(int tabIndex)
   {
      String divIndex = String.valueOf(tabIndex);
      selenium().selectFrame(
         "//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
            + "//table[@class='cke_editor']//iframe");
   }

   /**
    * Check what state of CK editor active: source or visual.
    * 
    * @param tabIndex
    * @param isSourceActive
    * @throws Exception
    */
   private void checkSourceAreaActiveInCkEditor(int tabIndex, boolean isSourceActive) throws Exception
   {
      String divIndex = String.valueOf(tabIndex);

      if (isSourceActive)
      {

         //  assertTrue(selenium().isElementPresent("//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
         //   + "//table[@class='cke_editor']//td[@class='cke_contents']/iframe"));

         assertTrue(selenium().isElementPresent(
            "//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
               + "//table[@class='cke_editor']//textarea"));

         assertFalse(selenium().isElementPresent(
            "//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
               + "//table[@class='cke_editor']//iframe"));
      }
      else
      {
         assertFalse(selenium().isElementPresent(
            "//div[@class='tabSetContainer']/div/div[" + divIndex + "]//table[@class='cke_editor']//textarea"));

         assertTrue(selenium().isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe"));
      }
   }

}
