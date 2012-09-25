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

      String LINENUMBER_PREFIX = "//div[@component='Border']/div/div/div/div/div[3]";

      String EDITOR_TABSET_LOCATOR = "//div[@id='editor']";

      String EDITOR_TAB_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]//div[@tabindex]";

      String EDITOR_VIEW_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s']";

      String SELECTED_EDITOR_TAB_LOCATOR = EDITOR_TABSET_LOCATOR
         + "//div[contains(@class, 'gwt-TabLayoutPanelTab-selected') and contains(., '%s')]";

      String JAVAEDITOR_SET_CURSOR_LOCATOR = EDITOR_VIEW_LOCATOR + "//div[@tabindex='-1']/div";

      String TITLE_SPAN_LOCATOR = "//span[@title='%s']";

      String GET_TEXT_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s']//div[@tabindex='-1']";

      String LINE_NUMBER_LOCATOR = "EDITOR_VIEW_LOCATOR + LINENUMBER_PREFIX";

      String GET_POSITION_TEXT = GET_TEXT_LOCATOR + "/div/div[%s]";
      
      String ERROR_LABEL_TEXT = "//div[text()='%s']";

      ////div[@panel-id='editor' and @view-id='editor-0']//div[@component='Border']/div/div/div/div/div[3]
   }

   private WebElement editor;

   //      @FindBy(xpath = Locators.LINE_HIGHLIGHTER_CLASS)
   //      private WebElement lineNumber;

   /**
    * wait appearance line number panel
    * @param tabIndex
    */
   public void waitLineNumberPanel(final int tabIndex)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement line = driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, tabIndex)));
               return line != null && line.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   
   /**
    * wait appearance error on 
    * panel with line numbers
    * if in java - mistake 
    * @param tabIndex
    */
   public void waitErrorLabel(final String errorMess)
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement textJavaErr = driver().findElement(By.xpath(String.format(Locators.ERROR_LABEL_TEXT, errorMess)));
               return textJavaErr!=null;
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }
   
   
   /**
    * wait appearance line number panel
    * @param tabIndex
    */
   public void waitLineCloseNumberPanel(final int tabIndex)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement line = driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, tabIndex)));
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
   * wait  line number panel disappear
   * @param tabIndex
   */
   public void waitFileContentModificationMark(final int tabIndex)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement line = driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, tabIndex)));
               return line != null && line.isDisplayed();
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

   //Should be complete later after applying not dynamic GWT class for this
   //   public void waitNoContentModificationMark(final String title)
//   {
//      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
//      {
//
//        
//      });
//   }

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
         WebElement eleme =
            driver().findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR + "//textarea", tabIndex)));
         eleme.sendKeys(text);

         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      finally
      {
         IDE().selectMainFrame();
      }
   }
   
   /**
    * Move cursor in Java editor left to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move left
    * @throws Exception
    */
   public void moveCursorLeft(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(tabIndex, Keys.ARROW_LEFT.toString());
      }
   }
   
   
   /**
    * Move cursor in Javaeditor right to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move right
    * @throws Exception
    */
   public void moveCursorRight(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(tabIndex, Keys.ARROW_RIGHT.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

   
   
   /**
    * Move cursor in Javaeditor down to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move right
    * @throws Exception
    */
   public void moveCursorDown(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(tabIndex, Keys.ARROW_DOWN.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }
   
   
   /**
    * Move cursor in Javaeditor up to pointed number of symbols.
    * 
    * @param tabIndex index of the tab
    * @param rows number of symbols to move right
    * @throws Exception
    */
   public void moveCursorUp(int tabIndex, int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(tabIndex, Keys.ARROW_UP.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }
   
   
   
   
   /**
    * emulate right click of a mouse
    * in java editor
    * @param tabIndex
    * @param text
    * @throws Exception
    */
   public void callContextMenuIntoJavaEditor(int tabIndex) throws Exception
   {
      try
      {
         WebElement eleme =
            driver().findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR, tabIndex)));
         new Actions(driver()).contextClick(eleme).build().perform();
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      finally
      {
         IDE().selectMainFrame();
      }
   }
   

   /**
    * get text from tag with java code in DOM
    * note: start position can be not with 1. 
    * Because text with java - code in DOM of the java editor not sequenced follows 
    * @param position
    * @return
    */
   public String getTextFromSetPosition(int tabIndex, int position)
   {
      return driver().findElement(By.xpath(String.format(Locators.GET_POSITION_TEXT, tabIndex, position))).getText();
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

   

   
}
