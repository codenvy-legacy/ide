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

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public class CkEditor extends AbstractTestModule
{
   public static final String MODIFIED_MARK = "*";

   private interface Locators
   {

      String CK_EDITOR = "//table[@class='cke_editor']";

      String CK_EDITOR_IFRAME = "td.cke_contents>iframe";

      String CK_EDITOR_TOOLS_BAR = "td#cke_top_editor%s";

      String CK_EDITOR_TOOL_SELECT =
         "//span[@class='cke_toolgroup']//span[@class='cke_button']//a[@href=\"javascript:void('%s')\"]";

      String CK_EDITOR_WYSWYG_TABLE = "table.cke_dialog_contents";

      String CK_EDITOR_OPENED = "//span[@id='cke_editor%s']" + "/span[@class='cke_browser_gecko cke_focus']";

      String EDITOR_TABSET_LOCATOR = "//div[@id='editor']";

      String TAB_LOCATOR = "//div[@tab-bar-index='%s']";

      String EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]";

      String DESIGN_BUTTON_ID = "DesignButtonID";

      String LINE_HIGHLIGHTER_CLASS = "CodeMirror-line-highlighter";

      String DESIGN_EDITOR_PREFIX = "//div[@view-id='editor-%s']";

      String IS_SWITCH_ON_CKEEDITOR =
         "//div[@view-id ='editor-%s']//div[@component='Border']/div/div/div/div[@style='position: absolute; overflow: hidden; z-index: 0; height: 100%; left: 0px; top: 0px; right: 0px; bottom: 0px; background-color: transparent; display: none;']";

      String WYSWYG_TABLE_SET_HEIGH = "//tr[@class='cke_dialog_ui_hbox']/td[2]//table/tbody/tr[2]//input";

      String WYSWYG_TABLE_OK_BTN = "//span[@class='cke_dialog_ui_button' and text()='OK']";

      String WYSWYG_TABLE_CANCEL_BTN = "//span[@class='cke_dialog_ui_button' and text()='Cancel']";

      //Locators for EditFileInWysiwygEditorTest test
      String CK_EDITOR_CREATED_TABLE =
         "//body[@class='cke_show_borders']//table[@cellspacing='1' and @cellpadding='1' and @style]/tbody/tr[%s]/td[%s]";

      String CONTEXT_MAIN_MENU_IFRAME = "//div[@class='cke_skin_ide'][1]//div/iframe";

      String CONTEXT_SUB_MENU_IFRAME = "//div[@class='cke_skin_ide'][2]//div/iframe";

      String CONTEXT_MAIN_MENU_ELEMENT = "//a[@title='%s']";

      String CONTEXT_SUB_MENU_ELEMENT = "//a[@title='%s']";

   }

   private WebElement editor;

   @FindBy(className = Locators.LINE_HIGHLIGHTER_CLASS)
   private WebElement highlighter;

   @FindBy(css = Locators.CK_EDITOR_WYSWYG_TABLE)
   private WebElement isCkEditorTable;

   @FindBy(xpath = Locators.WYSWYG_TABLE_SET_HEIGH)
   private WebElement heighWysywyngTable;

   @FindBy(xpath = Locators.WYSWYG_TABLE_OK_BTN)
   private WebElement okBtnWysywyngTable;

   @FindBy(xpath = Locators.CONTEXT_MAIN_MENU_IFRAME)
   private WebElement contextMainMenuFrame;

   @FindBy(xpath = Locators.CONTEXT_SUB_MENU_IFRAME)
   private WebElement contextSubMenuFrame;

   /**
    * wait tools panel in ck editor
    * @param numEdit
    */
   public void waitToolsCkEditor(final int numEdit)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement bar =
                  driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_TOOLS_BAR, numEdit)));
               return bar != null && bar.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   public void waitCkEditorTableOpen()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return isCkEditorTable != null && isCkEditorTable.isDisplayed() && heighWysywyngTable != null
               && heighWysywyngTable.isDisplayed();
         }
      });
   }

   /**
    * Delete all file content via Ctrl+a, Delete
    */
   public void deleteFileContentInCKEditor(int tabIndex) throws Exception
   {

      typeTextIntoCkEditor(tabIndex, Keys.CONTROL.toString() + "a" + Keys.DELETE.toString());

   }

   /**
    * Type text to file, opened in tab.
    * 
    * Index of tabs begins from 0.
    * 
    * Sometimes, if you can't type text to editor, try before to click on editor:
    * 
    * selenium().clickAt("//body[@class='editbox']", "5,5");
    * 
    * @param tabIndex begins from 0
    * @param text (can be used '\n' as line break)
    */
   public void typeTextIntoCkEditor(int tabIndex, String text) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      IDE().selectMainFrame();
      WebElement ckEditorIframe = driver().findElement(By.cssSelector(Locators.CK_EDITOR_IFRAME));
      driver().switchTo().frame(ckEditorIframe);
      driver().switchTo().activeElement().sendKeys(text);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE().selectMainFrame();
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
    * select tab star with 1 switch to iframe with ck_editor and return text into ck_editor
    * 
    * @param tabIndex
    * @return
    * @throws Exception
    */
   public String getTextFromCKEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      IDE().selectMainFrame();
      WebElement ckEditorIframe = driver().findElement(By.cssSelector(Locators.CK_EDITOR_IFRAME));
      driver().switchTo().frame(ckEditorIframe);
      String text = driver().switchTo().activeElement().getText();
      IDE().selectMainFrame();
      return text;
   }

   /**
    * Wait while tab appears in editor
    * 
    * @param tabIndex - index of tab, starts at 0
    * @throws Exception
    */
   public void waitTabPresent(int tabIndex) throws Exception
   {
      final String tab = Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_LOCATOR, tabIndex);

      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return input.findElement(By.xpath(tab)) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex index of tab, starts at 1
    * @throws Exception
    */
   public boolean WaitCkEditorOpened(final int tabIndex) throws Exception
   {
      return new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_OPENED, tabIndex))).isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex index of tab, starts at 1
    * @throws Exception
    */
   public boolean isCkEditorOpened(int tabIndex) throws Exception
   {
      try
      {
         return driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_OPENED, tabIndex))).isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Click on Design button at the bottom of editor.
    * 
    * @throws Exception
    */
   public void clickDesignButton() throws Exception
   {
      editor.findElement(By.id(Locators.DESIGN_BUTTON_ID)).click();
   }

   /**
    * click on tools on toolbar in ck editor
    * @param toolName
    */
   public void clickOnToolCkEditor(String toolName)
   {
      driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_TOOL_SELECT, toolName))).click();
   }

   /**
    * type text into height field the WYSYWYG table
    * @param toolName
    */
   public void typeToHeightwisiwyngtable(String value)
   {
      heighWysywyngTable.clear();
      heighWysywyngTable.sendKeys(value);
   }

   /**
    * click on ok button WYSYWYG table
    */
   public void clickOkWyswygTable()
   {
      okBtnWysywyngTable.click();
   }

   /**
    * switch to CkEditorIframe
    */
   public void switchToCkEditorIframe()
   {
      WebElement ckEditorIframe = driver().findElement(By.cssSelector(Locators.CK_EDITOR_IFRAME));
      driver().switchTo().frame(ckEditorIframe);
   }

   /**
    * check is table in ck-editor present
    * @param row
    * @param cell
    * @return
    */
   public boolean isTablePresent(final int row, final int cell)
   {
      WebElement table = driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_CREATED_TABLE, row, cell)));
      return table != null && table.isDisplayed();

   }

   /**
    * move cursor up on one position
    * @throws InterruptedException 
    */
   public void moveCursorUp() throws InterruptedException
   {
      new Actions(driver()).sendKeys(Keys.ARROW_UP.toString()).build().perform();
      //heed for set cursor in position
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
   }

   /**
    * 
    */
   public void switchToContextMenuIframe()
   {
      driver().switchTo().frame(contextMainMenuFrame);
   }

   /**
    * 
    */
   public void switchToContextSubMenuIframe()
   {
      driver().switchTo().frame(contextSubMenuFrame);
   }

   /**
    * wait sub menu in context menu
    * with param name
    * @param name
    */
   public boolean waitContextSubMenu(final String name)
   {
      return new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement elem = driver().findElement(By.xpath(String.format(Locators.CONTEXT_SUB_MENU_ELEMENT, name)));
               return elem.isDisplayed() && elem != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * @param name
    * @throws InterruptedException 
    */
   public void clickOnContextSubMenu(String name) throws InterruptedException
   {
    driver().findElement(By.xpath(String.format(Locators.CONTEXT_SUB_MENU_ELEMENT, name))).click();
    //heed for close context menu
    Thread.sleep(TestConstants.REDRAW_PERIOD);
    
   }

   /**
    * move cursor to context menu with
    * param name
    * @param titleMenu
    * @throws InterruptedException 
    */
   public void moveCursorToRowContextMenu(String titleMenu) throws InterruptedException
   {
      WebElement nameMenu =
         driver().findElement(By.xpath(String.format(Locators.CONTEXT_MAIN_MENU_ELEMENT, titleMenu)));
      new Actions(driver()).moveToElement(nameMenu).build().perform();
      //heed for open context submenu
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      
   }

   /**
    * click in table from ck-editor 
    * @param row
    * @param cell
    */
   public void clickOnCellTableCkEditor(final int row, final int cell)
   {
      WebElement table = driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_CREATED_TABLE, row, cell)));
      new Actions(driver()).moveToElement(table).build().perform();
      table.click();
   }

   /**
    * click in table from ck-editor 
    * @param row
    * @param cell
    * @throws InterruptedException 
    */
   public void callContextMenuCellTableCkEditor(final int row, final int cell) throws InterruptedException
   {
      WebElement table = driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_CREATED_TABLE, row, cell)));
      new Actions(driver()).contextClick(table).build().perform();

   }

}
