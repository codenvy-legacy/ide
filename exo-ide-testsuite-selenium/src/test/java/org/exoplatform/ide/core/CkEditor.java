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
import org.openqa.selenium.support.ui.ExpectedConditions;
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
      String CODE_MIRROR_EDITOR = "//body[@class='editbox']";

      String IFRAME_SELECTOR = "//div[@panel-id='editor']//div[@class='CodeMirror-wrapping']/iframe";

      String CK_EDITOR = "//table[@class='cke_editor']";

      String CK_EDITOR_IFRAME = "td#cke_contents_editor%s>iframe";

      String CK_EDITOR_TOOLS_BAR = "td#cke_top_editor%s";

      String CK_EDITOR_GET_TEXT = "cke_show_borders";

      String CK_EDITOR_TOOL_SELECT =
         "//span[@class='cke_toolgroup']//span[@class='cke_button']//a[@href=\"javascript:void('%s')\"]";

      String CK_EDITOR_OPENED = "//span[@id='cke_editor%s']" + "/span[@class='cke_browser_gecko cke_focus']";

      String CK_EDITOR_WYSWYG_TABLE = "table.cke_dialog_contents";

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

      // Locators for EditFileInWysiwygEditorTest test
      String CK_EDITOR_CREATED_TABLE =
         "//body[@class='cke_show_borders']//table[@cellspacing='1' and @cellpadding='1' and @style]/tbody/tr[%s]/td[%s]";

      String CONTEXT_MAIN_MENU_IFRAME = "//div[@class='cke_skin_ide'][1]//div/iframe";

      String CONTEXT_SUB_MENU_IFRAME = "//div[@class='cke_skin_ide'][2]//div/iframe";

      String CONTEXT_MAIN_MENU_ELEMENT = "//a[@title='%s']";

      String CONTEXT_SUB_MENU_ELEMENT = "//a[@title='%s']";

      String DESIGN_BUTTON_XPATH = "//div[@view-id='editor-%s']//div[@class='html-face' and text()='Design']";

      String GET_TEXT = "body.cke_show_borders";

      String CK_BOLD_TEXT_PREFIX = "body.cke_show_borders>strong";

      String CK_BOLDITALIC_TEXT_PREFIX = "body.cke_show_borders>em>strong";

      String CK_ITALIC_TEXT_PREFIX = "body.cke_show_borders>em";

      String NUM_ACTIVE_EDITOR = "//div[@class='gwt-TabLayoutPanelContent' and @is-active='true']";

   }

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

   @FindBy(className = Locators.CK_EDITOR_GET_TEXT)
   private WebElement ckTextContainer;

   @FindBy(xpath = Locators.CODE_MIRROR_EDITOR)
   private WebElement editorCodemirr;

   @FindBy(xpath = Locators.NUM_ACTIVE_EDITOR)
   private WebElement numActiveEditor;

   @FindBy(xpath = Locators.WYSWYG_TABLE_CANCEL_BTN)
   private WebElement tableCancelBtn;

   /**
    * wait tools panel in ck editor
    * 
    * @param numEdit
    */
   public void waitToolsCkEditor()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement bar =
                  driver().findElement(
                     By.cssSelector(String.format(Locators.CK_EDITOR_TOOLS_BAR, getActiveCkEditorTabIndex())));
               return bar != null && bar.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait text in ck editor
    * 
    * @param text
    * @param tabIndex
    */
   public void waitIsTextPresent(final String text)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return isTextPresent(text, getActiveCkEditorTabIndex());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait bold-italic text in ck editor
    * 
    * @param text
    * @param tabIndex
    */
   public void waitItalicBoldTextPresent(final String text)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return isBoldItalicTextPresent(text, getActiveCkEditorTabIndex());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait italic text in ck editor
    * 
    * @param text
    * @param tabIndex
    */
   public void waitItalicTextPresent(final String text)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return isItalicTextPresent(text, getActiveCkEditorTabIndex());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait bold text in ck editor
    * 
    * @param text
    * @param tabIndex
    */
   public void waitBoldTextPresent(final String text)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return isBoldTextPresent(text, getActiveCkEditorTabIndex());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * return true is text present
    * 
    * @param text
    */
   public boolean isTextPresent(String text, int tabIndex)
   {
      WebElement ckiframe = driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, tabIndex)));
      driver().switchTo().frame(ckiframe);
      String ckText = driver().findElement(By.cssSelector(Locators.GET_TEXT)).getText();
      IDE().selectMainFrame();
      return ckText.contains(text);

   }

   /**
    * return true is bold text fragment present
    * 
    * @param text
    */
   public boolean isItalicTextPresent(String text, int tabIndex)
   {
      WebElement ckiframe = driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, tabIndex)));
      driver().switchTo().frame(ckiframe);
      String boldText = driver().findElement(By.cssSelector(Locators.CK_ITALIC_TEXT_PREFIX)).getText();
      IDE().selectMainFrame();
      return boldText.contains(text);

   }

   /**
    * return true is bold text fragment present
    * 
    * @param text
    */
   public boolean isBoldItalicTextPresent(String text, int tabIndex)
   {
      WebElement ckiframe = driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, tabIndex)));
      driver().switchTo().frame(ckiframe);
      String boldText = driver().findElement(By.cssSelector(Locators.CK_BOLDITALIC_TEXT_PREFIX)).getText();
      IDE().selectMainFrame();
      return boldText.contains(text);

   }

   /**
    * return true is bold text fragment present
    * 
    * @param text
    */
   public boolean isBoldTextPresent(String text, int tabIndex)
   {
      WebElement ckiframe = driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, tabIndex)));
      driver().switchTo().frame(ckiframe);
      String boldText = driver().findElement(By.cssSelector(Locators.CK_BOLD_TEXT_PREFIX)).getText();
      IDE().selectMainFrame();
      return boldText.contains(text);

   }

   public void waitCkEditorTableOpen()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
    * 
    */
   public void deleteFileContentInCKEditor() throws Exception
   {

      typeTextIntoCkEditor(Keys.CONTROL.toString() + "a" + Keys.DELETE.toString());

   }

   /**
    * switch to iframe of the ck_editor and type text start with one
    * 
    * @param tabIndex
    * @param text
    * @throws Exception
    */
   public void typeTextIntoCkEditor(String text) throws Exception
   {
      WebElement ckiframe =
         driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, getActiveCkEditorTabIndex())));
      driver().switchTo().frame(ckiframe);
      ckTextContainer.sendKeys(text);
      IDE().selectMainFrame();
   }

   /**
    * Get the locator of content panel.
    * 
    * 
    * @param tabIndex
    *            starts from 0
    * @return content panel locator
    */
   public String getContentPanelLocator(int tabIndex)
   {
      return String.format(Locators.EDITOR_TAB_LOCATOR, tabIndex);
   }

   /**
    * Select iframe, which contains editor from tab with index tabIndex
    * 
    * @param tabIndex
    *            begins from 0
    */
   public void selectIFrameWithEditor(int tabIndex) throws Exception
   {
      String iFrameWithEditorLocator = getContentPanelLocator(tabIndex) + "//iframe";
      WebElement editorFrame = driver().findElement(By.xpath(iFrameWithEditorLocator));
      driver().switchTo().frame(editorFrame);
   }

   /**
    * Wait while tab appears in editor
    * 
    * @param tabIndex
    *            - index of tab, starts at 0
    * @throws Exception
    */
   public void waitTabPresent(int tabIndex) throws Exception
   {
      final String tab = Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_LOCATOR, tabIndex);

      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
    * waiting while switch between ckeditor on codeeditor
    * 
    * @param numCodeEditor
    */
   public void waitSwitchOnCodeEditor()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            IDE().selectMainFrame();
            WebElement elem = driver().findElement(By.xpath(Locators.IFRAME_SELECTOR));
            driver().switchTo().frame(elem);
            return editorCodemirr != null && editorCodemirr.isDisplayed();
         }
      });
   }

   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex
    *            index of tab, starts at 1
    * @throws Exception
    */
   public boolean waitCkEditorOpened(final int ckEditNum) throws Exception
   {
      return new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, ckEditNum)))
                  .isDisplayed();

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
    * @param tabIndex
    *            index of tab, starts at 1
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
      // click
      //TODO Check this 
      driver().findElement(By.xpath(String.format(Locators.DESIGN_BUTTON_XPATH, getActiveEditorTabIndex()))).click();
      driver().findElement(By.xpath(String.format(Locators.DESIGN_BUTTON_XPATH, getActiveEditorTabIndex()))).click();
      driver().findElement(By.xpath(String.format(Locators.DESIGN_BUTTON_XPATH, getActiveEditorTabIndex()))).click();
   }

   /**
    * click on tools on toolbar in ck editor
    * 
    * @param toolName
    */
   public void clickOnToolCkEditor(String toolName)
   {
      driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_TOOL_SELECT, toolName))).click();
   }

   /**
    * type text into height field the WYSYWYG table
    * 
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
    * click on cancel button WYSYWYG table
    */
   public void clickCancelWyswygTable()
   {
      tableCancelBtn.click();
   }

   /**
    * switch to CkEditorIframe
    */
   public void switchToCkEditorIframe(int ckEdit)
   {
      WebElement ckEditorIframe =
         driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, ckEdit)));
      driver().switchTo().frame(ckEditorIframe);
   }

   /**
    * wait is table in ck-editor present
    * 
    * @param row
    * @param cell
    * @return
    */
   public void waitTablePresent(int row, int cell)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.CK_EDITOR_CREATED_TABLE, row, cell))));
   }

   /**
    * move cursor up on one position
    * 
    * @throws InterruptedException
    */
   public void moveCursorUp() throws InterruptedException
   {
      new Actions(driver()).sendKeys(Keys.ARROW_UP.toString()).build().perform();
      // heed for set cursor in position
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
   }

   /**
    * 
    */
   public void switchToContextMenuIframe()
   {
      driver().switchTo().frame(contextMainMenuFrame);
   }

   /**
    * select tab star with 1 switch to iframe with ck_editor and return text
    * into ck_editor
    * 
    * @param tabIndex
    * @return text
    * @throws Exception
    */
   public String getTextFromCKEditor() throws Exception
   {
      WebElement ckiframe =
         driver().findElement(By.cssSelector(String.format(Locators.CK_EDITOR_IFRAME, getActiveCkEditorTabIndex())));
      driver().switchTo().frame(ckiframe);
      String txt = ckTextContainer.getText();
      IDE().selectMainFrame();
      return txt;
   }

   /**
    * 
    */
   public void switchToContextSubMenuIframe()
   {
      driver().switchTo().frame(contextSubMenuFrame);
   }

   /**
    * wait sub menu in context menu with param name
    * 
    * @param name
    */
   public void waitContextSubMenu(final String name)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.CONTEXT_SUB_MENU_ELEMENT, name))));
   }

   /**
    * wait sub menu in context menu with param name
    * 
    * @param name
    */
   public void waitContextMenu(final String name)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.CONTEXT_MAIN_MENU_ELEMENT, name))));
   }

   /**
    * @param name
    * @throws InterruptedException
    */
   public void clickOnContextSubMenu(String name) throws InterruptedException
   {
      driver().findElement(By.xpath(String.format(Locators.CONTEXT_SUB_MENU_ELEMENT, name))).click();
      // heed for close context menu
      Thread.sleep(TestConstants.REDRAW_PERIOD);

   }

   /**
    * move cursor to context menu with param name
    * 
    * @param titleMenu
    * @throws InterruptedException
    */
   public void moveCursorToRowContextMenu(String titleMenu) throws InterruptedException
   {
      WebElement nameMenu =
         driver().findElement(By.xpath(String.format(Locators.CONTEXT_MAIN_MENU_ELEMENT, titleMenu)));
      new Actions(driver()).moveToElement(nameMenu).build().perform();
      // heed for open context submenu
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);

   }

   /**
    * click in table from ck-editor
    * 
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
    * 
    * @param row
    * @param cell
    * @throws InterruptedException
    */
   public void callContextMenuCellTableCkEditor(final int row, final int cell) throws InterruptedException
   {
      WebElement table = driver().findElement(By.xpath(String.format(Locators.CK_EDITOR_CREATED_TABLE, row, cell)));
      new Actions(driver()).contextClick(table).build().perform();

   }

   /**
    * Getting of index of current active CkEditor
    * 
    * @return Active tab index + 1 for CkEditor.
    */
   public int getActiveCkEditorTabIndex()
   {
      return Integer.parseInt(numActiveEditor.getAttribute("view-id").replace("editor-", "")) + 1;
   }

   /**
   * Getting of index of current active editor
   * 
   * @return Active editor tab index
   */
   public int getActiveEditorTabIndex()
   {
      return Integer.parseInt(numActiveEditor.getAttribute("view-id").replace("editor-", ""));
   }

}
