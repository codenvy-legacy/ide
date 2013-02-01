package org.exoplatform.ide.core;

import java.util.Arrays;

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

      String TITLE_SPAN_LOCATOR = "//span[@title='%s']/..";

      String GET_TEXT_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s']//div[@tabindex='-1']";

      String LINE_NUMBER_LOCATOR = EDITOR_VIEW_LOCATOR + LINENUMBER_PREFIX;

      String GET_POSITION_TEXT = GET_TEXT_LOCATOR + "/div/div[%s]";

      String ERROR_LABEL_TEXT = "//div[text()='%s']";

      String JAVA_DOC_CONTAINER = "//div[@__animcontrollerstate]";

      String NUM_ACTIVE_EDITOR = "//div[@class='gwt-TabLayoutPanelContent' and @is-active='true']";

      String HIGHLITER_BORDER_PREFIX = "//div[@component='Border' and contains(@style, '182, 204, 232')]";

      String ACTIVE_EDITOR_WITH_REDACTOR = NUM_ACTIVE_EDITOR + HIGHLITER_BORDER_PREFIX + "/div/div//textarea";

      String EDITOR_JAVA_CONTAINER_READY_STATUS = "//div[@class='gwt-TabLayoutPanelContent' and @is-active='true']//..";

      String EDITOR_CONTAINER_READY_DESC =
         "//div[@class='gwt-TabLayoutPanelContent' and @is-active='true']/parent::div/preceding-sibling::div[1]";
   }

   private WebElement editor;

   @FindBy(xpath = Locators.JAVA_DOC_CONTAINER)
   private WebElement javaDocContainer;

   @FindBy(xpath = Locators.ACTIVE_EDITOR_WITH_REDACTOR)
   private WebElement numActiveEditorWithRedactor;

   @FindBy(xpath = Locators.EDITOR_JAVA_CONTAINER_READY_STATUS)
   private WebElement javacontainerReadyStatus;

   @FindBy(xpath = Locators.EDITOR_CONTAINER_READY_DESC)
   private WebElement descendReadyStatus;

   @FindBy(xpath = Locators.NUM_ACTIVE_EDITOR)
   private WebElement numActiveEditor;

   //   /**
   //    * wait appearance line number panel
   //    * 
   //    */
   //   public void waitLineNumberPanel()
   //   {
   //      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
   //      {
   //
   //         @Override
   //         public Boolean apply(WebDriver driver)
   //         {
   //            try
   //            {
   //               WebElement line =
   //                  driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, getNumberTabOfActiveEditor())));
   //               return line != null && line.isDisplayed();
   //            }
   //            catch (Exception e)
   //            {
   //               return false;
   //            }
   //         }
   //      });
   //   }

   /**
    * wait while java editor is active
    */
   public void waitJavaEditorIsActive()
   {
      new WebDriverWait(driver(), 180).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            return javacontainerReadyStatus.getAttribute("style").contains("width: 100%; height: 100%;")
               && !descendReadyStatus.getAttribute("style").isEmpty() && numActiveEditorWithRedactor.isDisplayed();
         }
      });
   }

   /**
    * wait while into java editor appear text
    */
   public void waitIntoJavaEditorSpecifiedText(final String text)
   {
      new WebDriverWait(driver(), 20).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return getTextFromJavaEditor().contains(text);
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait appearance error on panel with line numbers if in java - mistake
    * 
    */
   public void waitErrorLabel(final String errorMess)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement textJavaErr =
                  driver().findElement(By.xpath(String.format(Locators.ERROR_LABEL_TEXT, errorMess)));
               return textJavaErr != null;
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
    * 
    */
   public void waitLineCloseNumberPanel()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement line =
                  driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, getNumberTabOfActiveEditor())));
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
    * wait line number panel disappear
    * 
    */
   public void waitFileContentModificationMark()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement line =
                  driver.findElement(By.xpath(String.format(Locators.LINE_NUMBER_LOCATOR, getNumberTabOfActiveEditor())));
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
    * wait appereance javadoc Container
    */
   public void waitJavaDocContainer()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            return javaDocContainer != null && javaDocContainer.isDisplayed();
         }
      });
   }

   /**
    * wait appereance javadoc Container with specified javadoc text
    */
   public void waitJavaDocContainerWithSpecifiedText(final Object[] obj)
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            waitJavaDocContainer();
            String[] stringArray = Arrays.copyOf(obj, obj.length, String[].class);
            String compare1 = convertObjToStr(stringArray);
            String compare2 = convertObjToStr(getTextFromJavaDocContainer().split("\n"));
            return compare1.contains(compare2);
         }
      });
   }

   /**
    * auxiliary for cast arraystring  to string
    * @param arr
    * @return
    */
   protected String convertObjToStr(String[] arr)
   {
      StringBuilder builderForObj = new StringBuilder();
      for (String s : arr)
      {
         builderForObj.append(s);

      }
      return builderForObj.toString();
   }

   /**
    * get text from javadoccontainer
    * 
    * @return
    */
   public String getTextFromJavaDocContainer()
   {
      return javaDocContainer.getText();
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
    * set cursor in begin position in java editor
    */
   public void setCursorToJavaEditor() throws Exception
   {
      driver().findElement(
         By.xpath(String.format(Locators.JAVAEDITOR_SET_CURSOR_LOCATOR, getNumberTabOfActiveEditor()))).click();
   }

   /**
    * @param text
    *            (can be used '\n' as line break)
    */
   public void typeTextIntoJavaEditor(String text) throws Exception
   {
      try
      {
         WebElement eleme =
            driver().findElement(
               By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR + "//textarea", getNumberTabOfActiveEditor())));
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
    * @param rows
    *            number of symbols to move left
    * @throws Exception
    */
   public void moveCursorLeft(int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(Keys.ARROW_LEFT.toString());
      }
   }

   /**
    * Move cursor in Javaeditor right to pointed number of symbols.
    * 
    * @param rows
    *            number of symbols to move right
    * @throws Exception
    */
   public void moveCursorRight(int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(Keys.ARROW_RIGHT.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

   /**
    * Move cursor in Javaeditor down to pointed number of symbols.
    * 
    * @param rows
    *            number of symbols to move right
    * @throws Exception
    */
   public void moveCursorDown(int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(Keys.ARROW_DOWN.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

   /**
    * Move cursor in Javaeditor up to pointed number of symbols.
    * 
    * @param rows
    *            number of symbols to move right
    * @throws Exception
    */
   public void moveCursorUp(int symbols) throws Exception
   {
      for (int i = 0; i < symbols; i++)
      {
         typeTextIntoJavaEditor(Keys.ARROW_UP.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

   /**
    * emulate right click of a mouse in java editor
    * 
    * @param text
    * @throws Exception
    */
   public void callContextMenuIntoJavaEditor() throws Exception
   {
      try
      {
         WebElement eleme =
            driver().findElement(By.xpath(String.format(Locators.EDITOR_VIEW_LOCATOR, getNumberTabOfActiveEditor())));
         new Actions(driver()).contextClick(eleme).build().perform();
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      finally
      {
         IDE().selectMainFrame();
      }
   }

   /**
    * @param title
    */
   public void waitNoContentModificationMark(final String title)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
    * Wait mark of file content modification appear (symbol "*" near title).
    * 
    * @param title
    *            file's title
    */
   public void waitFileContentModificationMark(final String title)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
    * get text from tag with java code in DOM note: start position can be not
    * with 1. Because text with java - code in DOM of the java editor not
    * sequenced follows
    * 
    * @param position
    * @return
    */
   public String getTextFromSetPosition(int position)
   {
      return driver().findElement(
         By.xpath(String.format(Locators.GET_POSITION_TEXT, getNumberTabOfActiveEditor(), position))).getText();
   }

   public String getTextFromJavaEditor() throws Exception
   {
      String text =
         driver().findElement(By.xpath(String.format(Locators.GET_TEXT_LOCATOR, getNumberTabOfActiveEditor())))
            .getText();
      return text;
   }

   /**
    * Getting of number current active tab of the code editor
    * 
    * @return
    */
   public int getNumberTabOfActiveEditor()
   {
      return Integer.parseInt(numActiveEditor.getAttribute("view-id").replace("editor-", ""));
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

}
