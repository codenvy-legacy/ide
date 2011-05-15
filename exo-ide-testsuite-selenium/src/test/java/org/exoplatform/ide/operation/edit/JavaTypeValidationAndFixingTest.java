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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class JavaTypeValidationAndFixingTest extends BaseTest
{

   private final static String SERVICE_FILE_NAME = "java-type-validation-and-fixing.groovy";

   private final static String TEMPLATE_FILE_NAME = "java-type-validation-and-fixing.gtmpl";

   private final static String TEST_FOLDER = JavaTypeValidationAndFixingTest.class.getSimpleName();

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      String serviceFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + SERVICE_FILE_NAME;
      String templateFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + TEMPLATE_FILE_NAME;

      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(serviceFilePath, MimeType.GROOVY_SERVICE, URL + TEST_FOLDER + "/"
            + SERVICE_FILE_NAME);
         VirtualFileSystemUtils.put(templateFilePath, MimeType.GROOVY_TEMPLATE, URL + TEST_FOLDER + "/"
            + TEMPLATE_FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }

   }

   //   IDE-436: "Recognize error "cannot resolve to a type" within the POGO file or REST service file in the Code Editor."
   //   Revalidate code and update error marks after the next events (IDE-436)
   //   file creation,
   //   file reopening,
   //   opening file by path,
   //   opening file from file history,
   //   opening local file,
   //   opening file from template,
   //   after the lineNubers field is turned on.
   //   
   //   There is list of java type errors:
   //      line 8: 'POST' cannot be resolved to a type;
   //      line 10: 'List' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'Map' cannot be resolved to a type;
   //      line 11: 'List' cannot be resolved to a type;

   @Test
   public void testServiceFile() throws Exception
   {
      // Open groovy file with test content
      waitForRootElement();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + SERVICE_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);

      // test error marks
      firstTestErrorMarks();

      // fix error
      selenium
         .clickAt(
            getCodeErrorMarkLocator(
               15,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; "),
            "");
      selenium.click(getErrorCorrectionListItemLocator("Base64"));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);

      // test import statement
      IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import Path\n" + "import javax.ws.rs.GET\n" + "import some.pack.String\n"
            + "import java.util.prefs.Base64\n" + "\n" + "@Path("));

      // test code error marks
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(16,
         "'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(17)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(32)));

      // edit text
      IDE.EDITOR.deleteFileContent();
      IDE.EDITOR.waitTabPresent(0);

      // test removing error marks if file is empty
      // assertFalse(selenium.getEval("this.browserbot.findElement(\"//div[@class=\'CodeMirror-line-numbers\']/div[text() = \'9\']\").hasAttribute(\"title\")") == "true");           

      // add test text
      IDE.EDITOR.typeTextIntoEditor(0, "Integer1 d \n" + "@POST \n"
         + "public Base64 hello(@PathParam(\"name\") Base64 name) {}");
      IDE.EDITOR.waitTabPresent(0);

      // test error marks
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(1, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(2, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium
         .isElementPresent(getCodeErrorMarkLocator(
            3,
            "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'Base64' cannot be resolved to a type; ")));

      // fix error
      selenium.clickAt(getCodeErrorMarkLocator(3), "");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP * 2);

      // test import statement and code error marks
      IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "import java.util.prefs.Base64\n" + "Integer1 d \n" + "@POST \n"
            + "public Base64 hello(@PathParam(\"name\") Base64 name) {}"));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(2, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(3, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(4, "'PathParam' cannot be resolved to a type; ")));
      // turn off line numbers

      //TODO after fix problem in Issue IDE - 764 code must be uncommented
      /*      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_HIDE_NUMBERS);

            // turn on line numbers and test error marks
            IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
            Thread.sleep(TestConstants.SLEEP);
            secondVerificationOfErrorMarks();

            // save text, reopen and test error marks
            saveCurrentFile();
            IDE.EDITOR.closeTab(0);
            IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(SERVICE_FILE_NAME, false);
            Thread.sleep(TestConstants.SLEEP);
            secondVerificationOfErrorMarks();

            // refresh browser and test error marks
            refresh();
            IDE.EDITOR.clickOnEditor();
            Thread.sleep(TestConstants.SLEEP);
            secondVerificationOfErrorMarks();
      */
   }

   private void firstTestErrorMarks()
   {
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(4)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(6)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(7)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(8)));

      assertTrue(selenium
         .isElementPresent(getCodeErrorMarkLocator(
            9,
            "'ManyToOne' cannot be resolved to a type; 'Mandatory' cannot be resolved to a type; 'org.chromattic.MappedBy' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(10, "'Property' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(13, "'POST' cannot be resolved to a type; ")));

      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(14)));

      assertTrue(selenium
         .isElementPresent(getCodeErrorMarkLocator(
            15,
            "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(16, "'Base64' cannot be resolved to a type; ")));

      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(17)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(18)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(19)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(20)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(21)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(23)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(24)));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(31,
         "'java.util.prefs.Base64' cannot be resolved to a type; ")));
   }

   private void secondVerificationOfErrorMarks()
   {
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(1, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(2, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(3, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(4, "'PathParam' cannot be resolved to a type; ")));
   }

   // IDE-499: "Recognize error "cannot resolve to a type" within the Groovy Template file in the Code Editor."
   @Test
   public void testTemplateFile() throws Exception
   {
      // Open template file with test content
      waitForRootElement();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + TEMPLATE_FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(1);

      // test error marks
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(6, "'Path' cannot be resolved to a type; ")));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(7)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(8)));

      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(9, "'POST' cannot be resolved to a type; ")));

      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(10, "'Path' cannot be resolved to a type; ")));

      assertTrue(selenium
         .isElementPresent(getCodeErrorMarkLocator(
            11,
            "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(12, "'Base64' cannot be resolved to a type; ")));

      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(13)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(14)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(17)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(19)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(21)));
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(23,
         "'java.util.prefs.Base64' cannot be resolved to a type; ")));

      // fix error
      selenium
         .clickAt(
            getCodeErrorMarkLocator(
               11,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; "),
            "");
      IDE.EDITOR.waitTabPresent(1);
      selenium.clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      IDE.EDITOR.waitTabPresent(1);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);

      // test import statement
      IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(1).startsWith("<%\n" + "  import java.util.prefs.Base64\n" + "%>\n"));

      // test code error marks
      assertTrue(selenium.isElementPresent(getCodeErrorMarkLocator(14,
         "'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(15)));
      assertFalse(selenium.isElementPresent(getCodeErrorMarkLocator(23)));
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.EDITOR.closeFileTabIgnoreChanges(1);
      IDE.EDITOR.closeTab(0);

      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   public String getCodeErrorMarkLocator(int lineNumber, String title)
   {
      return "//div[@class='CodeMirror-line-numbers']/div[text() = '" + lineNumber + "' and @title=\"" + title + "\"]";
   }

   public String getCodeErrorMarkLocator(int lineNumber)
   {
      return "//div[@class='CodeMirror-line-numbers']/div[text() = '" + lineNumber + "' and @title]";
   }

   private String getErrorCorrectionListItemLocator(String packageName)
   {
      return "//div[@class='gwt-Label' and contains(text(),'" + packageName + "')]";
   }
}