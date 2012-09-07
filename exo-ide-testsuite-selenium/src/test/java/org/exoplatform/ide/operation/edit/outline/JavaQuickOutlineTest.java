/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import junit.framework.Assert;

import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.operation.autocompletion.java.JavaCodeAssistantTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaQuickOutlineTest extends CodeAssistantBaseTest
{
   private static final String FILE_NAME = "GreetingController.java";

   private static final String QUICK_OUTLINE_PANEL = "ideQuickOutlinePopup";

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         createProject(JavaCodeAssistantTest.class.getSimpleName(),
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/JavaTestProject.zip");
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
      openProject();
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + "pom.xml");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.CODE_ASSISTANT_JAVA.waitForJavaToolingInitialized(FILE_NAME);
   }

   @Test
   public void quickOutline() throws Exception
   {
      IDE.GOTOLINE.goToLine(24);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.chord(Keys.CONTROL, "o"));
      waitForQuickOutlineOpened();
      assertElementPresent("helloworld");
      assertElementPresent("GreetingController");
      assertElementPresent("handleRequest(HttpServletRequest, HttpServletResponse)");

      selectItem("GreetingController");
      waitForQuickOutlineClosed();
      Assert.assertEquals("12 : 1", IDE.STATUSBAR.getCursorPosition());
   }

   /**
    * 
    */
   private void waitForQuickOutlineClosed()
   {
      (new WebDriverWait(IDE.driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               driver.findElement(By.id(QUICK_OUTLINE_PANEL));
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
    * @param string
    */
   private void selectItem(String name)
   {
      String expression = "//div[@id='ideQuickOutlineTree']//span[text()='" + name + "']";
      WebElement element = driver.findElement(By.xpath(expression));
      element.click();
      element = driver.findElement(By.xpath(expression));
      //      element.click();
      //      element.click();
      new Actions(driver).doubleClick(element).perform();
   }

   /**
    * @param string
    */
   private void assertElementPresent(String name)
   {
      assertNotNull(IDE.driver().findElement(By.xpath("//div[@id='ideQuickOutlineTree']//span[text()='" + name + "']")));
   }

   /**
    * 
    */
   private void waitForQuickOutlineOpened()
   {
      (new WebDriverWait(IDE.driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            return d.findElement(By.id(QUICK_OUTLINE_PANEL)) != null;
         }
      });
   }

}
