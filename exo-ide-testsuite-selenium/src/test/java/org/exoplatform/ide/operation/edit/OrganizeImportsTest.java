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
package org.exoplatform.ide.operation.edit;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.operation.autocompletion.java.JavaCodeAssistantTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OrganizeImportsTest extends CodeAssistantBaseTest
{
   private static final String FILE_NAME = "GreetingController.java";
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
      IDE.CODEASSISTANT.waitForJavaToolingInitialized(FILE_NAME);
   }

   @Test
   public void organizeImportTest() throws Exception
   {
      //step 1 check organize import
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "O");
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).doesNotContain("import java.util.HashMap;").doesNotContain(
         "import java.util.Map;");

      //step 3 add two new objects
      IDE.GOTOLINE.goToLine(21);
      IDE.EDITOR.typeTextIntoEditor(0, "List a;\n Label b;");
      //To editor parse text 
      Thread.sleep(TestConstants.SLEEP);

      //step 3 organize import for new objects
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "O");
      IDE.ORGINIZEIMPORT.waitForWindowOpened();
      String ss = IDE.ORGINIZEIMPORT.getTextFromImportList();
      assertThat(ss).contains("com.sun.xml.internal.bind.v2.schemagen.xmlschema.List").contains("java.util.List")
         .contains("java.awt.List");
      
      //driver.findElement(By.id("ideOrganizeImportNext")).click();
      IDE.ORGINIZEIMPORT.nextBtnclick();
      IDE.ORGINIZEIMPORT.waitForValueInImportList("com.sun.xml.internal.ws.org.objectweb.asm.Label");
      IDE.ORGINIZEIMPORT.selectValueInImportList("com.sun.xml.internal.ws.org.objectweb.asm.Label");
      IDE.ORGINIZEIMPORT.finishBtnclick();
      //driver.findElement(By.id("ideOrganizeImportFinish")).click();
      IDE.ORGINIZEIMPORT.waitForWindowClosed();
      //To editor parse text 
      Thread.sleep(TestConstants.SLEEP);
      
      //step 4 check complete of the organize import
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains(
         "import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;").contains(
         "import com.sun.xml.internal.ws.org.objectweb.asm.Label;");
   }

}
