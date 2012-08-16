package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JavaEditor extends AbstractTestModule
{
   public static final String MODIFIED_MARK = "*";

   private interface Locators
   {
      String EDITOR_TABSET_LOCATOR = "//div[@id='editor']";

      String TAB_LOCATOR = "//div[@tab-bar-index='%s']";

      String EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]//div[@tabindex]";

      String EDITOR_VIEW_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s']";

      String ACTIVE_EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and @is-active='true']";

      String SELECTED_EDITOR_TAB_LOCATOR = EDITOR_TABSET_LOCATOR
         + "//div[contains(@class, 'gwt-TabLayoutPanelTab-selected') and contains(., '%s')]";

      String DEBUG_EDITOR_ACTIVE_FILE_URL = "debug-editor-active-file-url";

      String DEBUG_EDITOR_PREVIOUS_ACTIVE_FILE_URL = "debug-editor-previous-active-file-url";

      String JAVAEDITOR_SET_CURSOR_LOCATOR = EDITOR_VIEW_LOCATOR + "//div[@tabindex='-1']/div";

      String JAVAEDITOR_SET_LOCATOR = EDITOR_VIEW_LOCATOR + "//div[@tabindex='-1']";

      String TAB_TITLE = "//table[@class='tabTitleTable' and contains(., '%s')]";

      String CLOSE_BUTTON_LOCATOR = "//div[@button-name='close-tab']";

      String VIEW_ID_ATTRIBUTE = "view-id";

      String TITLE_SPAN_LOCATOR = "//span[@title='%s']";

      String LINE_NUMBER_CSS_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]//div[@style and @class]";

      String ACTIVE_FILE_ID = "debug-editor-active-file-url";

      String LINE_HIGHLIGHTER_CLASS = "CodeMirror-line-highlighter";

      String HIGHLIGHTER_SELECTOR = "div[view-id=editor-%s] div." + LINE_HIGHLIGHTER_CLASS;

      String GET_TEXT_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s']//div[@tabindex='-1']";

   }

   private WebElement editor;

   @FindBy(className = Locators.LINE_HIGHLIGHTER_CLASS)
   private WebElement highlighter;

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
      return tab.getText().trim();
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
      editor.findElement(
         By.xpath(String.format(Locators.EDITOR_TABSET_LOCATOR + Locators.TAB_LOCATOR + "//span", tabIndex))).click();
      // TODO replace with wait for condition
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Select tab the code editor with the specified name
    * 
    * @param fileName
    * @throws Exception
    */
   public void selectTab(String fileName) throws Exception
   {
      WebElement tab =
         editor.findElement(By.xpath(String.format(Locators.EDITOR_TABSET_LOCATOR + Locators.TITLE_SPAN_LOCATOR,
            fileName)));
      tab.click();

      // TODO replace with wait for condition
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * click on save as button and type in save as field new name of the file
    * 
    * @param tabIndex
    * @param name
    * @throws Exception
    */
   public void saveAs(int tabIndex, String name) throws Exception
   {
      selectTab(tabIndex);

      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);
      IDE().ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE().ASK_FOR_VALUE_DIALOG.setValue(name);
      IDE().ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE().ASK_FOR_VALUE_DIALOG.waitClosed();
   }

   /**
    * Click on Close Tab button. Old name of this method is "clickCloseTabButton(int tabIndex)"
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
    * click on close label on tab wit file name
    * 
    * @param tabTitle
    * @throws Exception
    */
   public void clickCloseEditorButton(String tabTitle) throws Exception
   {
      WebElement closeButton =
         editor.findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_TITLE, tabTitle)
            + Locators.CLOSE_BUTTON_LOCATOR));
      closeButton.click();
   }

   /**
    * Closes file with num tabinfex start with 0
    * 
    * @param tabIndex
    */
   public void closeFile(int tabIndex) throws Exception
   {
      selectTab(tabIndex);
      String activeFile =
         (selenium().getText(Locators.ACTIVE_FILE_ID) == null) ? "" : selenium().getText(Locators.ACTIVE_FILE_ID);
      clickCloseEditorButton(tabIndex);
      waitActiveFileChanged(activeFile);
   }

   /**
    * Close file with name on tab
    * 
    * @param fileName
    * @throws Exception
    */
   public void closeFile(String fileName) throws Exception
   {
      selectTab(fileName);
      String activeFile =
         (selenium().getText(Locators.ACTIVE_FILE_ID) == null) ? "" : selenium().getText(Locators.ACTIVE_FILE_ID);
      clickCloseEditorButton(fileName);
      waitActiveFileChanged(activeFile);
      waitTabNotPresent(fileName);
   }

   /**
    * @param activeFile
    */
   private void waitActiveFileChanged(final String activeFile)
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return !activeFile.equals(selenium().getText(Locators.ACTIVE_FILE_ID));
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

      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
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

   /**
    * 
    * 
    * @param tabIndex index of tab, starts at 0
    * @throws Exception
    */
   public void forcedClosureFile(int tabIndex) throws Exception
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

         new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
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

   /**
    * 
    * 
    * @param tabIndex index of tab, starts at 0
    * @return
    */
   public boolean isFileContentChanged(int tabIndex)
   {
      final String tabName = getTabTitle(tabIndex);
      return tabName.endsWith(MODIFIED_MARK);
   }

   public boolean isFileContentChanged(String title)
   {
      WebElement tab =
         editor
            .findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TITLE_SPAN_LOCATOR, title)));
      return tab.getText().trim().endsWith(MODIFIED_MARK);
   }

   /**
    * Wait mark of file content modification appear (symbol "*" near title).
    * 
    * @param title file's title
    */
   public void waitFileContentModificationMark(final String title)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement tab =
                  editor.findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR
                     + String.format(Locators.TITLE_SPAN_LOCATOR, title)));
               return tab.getText().trim().endsWith(MODIFIED_MARK);
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * @param title
    */
   public void waitNoContentModificationMark(final String title)
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement tab =
                  editor.findElement(By.xpath(Locators.EDITOR_TABSET_LOCATOR
                     + String.format(Locators.TITLE_SPAN_LOCATOR, title)));
               return !tab.getText().trim().endsWith(MODIFIED_MARK);
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Close new file. If saveFile true - save file. If fileName is null - save with default name, else save with fileName name.
    * 
    * @param tabIndex - index of tab in editor panel
    * @param saveFile - is save file before closing
    * @param fileName - name of new file
    * @throws Exception
    */
   public void saveAndCloseFile(int tabIndex, String newFileName) throws Exception
   {
      selectTab(tabIndex);
      final String viewId =
         editor.findElement(By.xpath(Locators.ACTIVE_EDITOR_TAB_LOCATOR)).getAttribute(Locators.VIEW_ID_ATTRIBUTE);
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

      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
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
      try
      {
         return editor.findElement(By.xpath(String.format(Locators.SELECTED_EDITOR_TAB_LOCATOR, tabTitle))) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Returns the active state of the editor. Index starts from <code>0</code>.
    * 
    * @param editorIndex editor's index
    * @return {@link Boolean} <code>true</code> if active
    */
   public boolean isActive(int editorIndex)
   {
      WebElement view = editor.findElement(By.xpath(String.format(Locators.EDITOR_TAB_LOCATOR, editorIndex)));
      return IDE().PERSPECTIVE.isViewActive(view);

   }

   /**
    * set cursor in begin position in java editor
    */
   public void setCursorToJavaEditor(int tabIndex) throws Exception
   {
      driver().findElement(By.xpath(String.format(Locators.JAVAEDITOR_SET_CURSOR_LOCATOR, tabIndex))).click();
   }

   /**
    * @param tabIndex begins from 1
    * @param text (can be used '\n' as line break)
    */
   public void typeTextIntoJavaEditor(int tabIndex, String text) throws Exception
   {
      try
      {
         new Actions(driver()).sendKeys().build().perform();
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      finally
      {
         IDE().selectMainFrame();
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
    * Get text from tab number "tabIndex" from javaeditor
    * 
    * @param tabIndex begins from 0
    */
   public String getTextFromJavaEditor(int tabIndex) throws Exception
   {
      String text = driver().findElement(By.xpath(String.format(Locators.GET_TEXT_LOCATOR, tabIndex))).getText();
      return text;
   }

   /**
    * select tab star with 1 switch to iframe with ck_editor and return text into ck_editor
    * 
    * @param tabIndex
    * @return
    * @throws Exception
    */

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
    * Wait while tab appears in editor
    * 
    * @param tabIndex - index of tab, starts at 0
    * @throws Exception
    */
   public void waitActiveFile(String path) throws Exception
   {
      final String location = (path.startsWith("/")) ? path : "/" + path;
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return location.equals(selenium().getText(Locators.ACTIVE_FILE_ID));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait while tab disappears in editor
    * 
    * @param tabIndex - index of tab, starts at 0
    * @throws Exception
    */
   public void waitTabNotPresent(int tabIndex) throws Exception
   {
      final String tab = Locators.EDITOR_TABSET_LOCATOR + String.format(Locators.TAB_LOCATOR, tabIndex);

      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(tab));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public void waitTabNotPresent(String fileName) throws Exception
   {
      final String tab = String.format(Locators.EDITOR_TABSET_LOCATOR + Locators.TITLE_SPAN_LOCATOR, fileName);

      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(tab));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public boolean isLineNumbersVisible()
   {
      try
      {
         return editor.findElement(By.cssSelector(Locators.LINE_NUMBER_CSS_LOCATOR)) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   public String getSelectedText(int tabIndex) throws Exception
   {

      // TODO find how to get selected text
      String text = selenium().getEval("if (window.getSelection()) { window.getSelection().toString();}");
      IDE().selectMainFrame();
      return text;
   }

   public boolean isHighlighterPresent()
   {
      return highlighter != null && highlighter.isDisplayed();
   }

   /**
    * @param tabIndex editor tab with highlighter
    * @return {@link WebElement} highlighter
    */
   public WebElement getHighlighter(int tabIndex)
   {
      try
      {
         return editor.findElement(By.cssSelector(String.format(Locators.HIGHLIGHTER_SELECTOR, tabIndex)));
      }
      catch (NoSuchElementException e)
      {
         return null;
      }
   }

   /**
    * Open editor's context menu
    * 
    * @param editorIndex
    * @throws Exception
    */
   public void openContextMenu(int editorIndex) throws Exception
   {

      IDE().selectMainFrame();
   }
}
