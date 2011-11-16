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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class JavaTypeValidationAndFixingTest extends BaseTest
{

   private final static String SERVICE_FILE_NAME = "java-type-validation-and-fixing.groovy";

   private final static String TEMPLATE_FILE_NAME = "java-type-validation-and-fixing.gtmpl";

   private final static String PROJECT = JavaTypeValidationAndFixingTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String serviceFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + SERVICE_FILE_NAME;
      String templateFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + TEMPLATE_FILE_NAME;

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, SERVICE_FILE_NAME, MimeType.GROOVY_SERVICE, serviceFilePath);
         VirtualFileSystemUtils.createFileFromLocal(link, TEMPLATE_FILE_NAME, MimeType.GROOVY_TEMPLATE,
            templateFilePath);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testServiceFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SERVICE_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + SERVICE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SERVICE_FILE_NAME);

      // test error marks
      firstTestErrorMarks();

      // test error marks appearance after the hiding line numbers (IDE-764)
      assertTrue(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      assertTrue(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS));
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);

      // test error marks
      firstTestErrorMarks();

      // fix error
      selenium()
         .clickAt(
            getCodeErrorMarkLocator(
               16,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; "),
            "");
      selenium().click(getErrorCorrectionListItemLocator("Base64"));
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());

      // test import statement
      IDE.EDITOR.typeTextIntoEditor(0, Keys.PAGE_UP.toString() + Keys.HOME);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import Path\n" + "import javax.ws.rs.GET\n" + "import some.pack.String\n"
            + "import javax.inject.Inject \n" + "import java.util.prefs.Base64\n" + "\n" + "@Path("));

      // test code error marks
      assertTrue(selenium().isElementPresent(
         getCodeErrorMarkLocator(17,
            "'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(18)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(37)));

      // edit text
      IDE.EDITOR.deleteFileContent(0);

      // add test text
      IDE.EDITOR.typeTextIntoEditor(0, "Integer1 d \n" + "@POST \n"
         + "public Base64 hello(@PathParam(\"name\") Base64 name) {}");

      // test error marks
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(1, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(2, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium()
         .isElementPresent(
            getCodeErrorMarkLocator(
               3,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'Base64' cannot be resolved to a type; ")));

      // fix error
      selenium().clickAt(getCodeErrorMarkLocator(3), "");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium().clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP * 2);

      // test import statement and code error marks
      IDE.EDITOR.typeTextIntoEditor(0, Keys.PAGE_UP.toString() + Keys.HOME);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "import java.util.prefs.Base64\n" + "Integer1 d \n" + "@POST \n"
            + "public Base64 hello(@PathParam(\"name\") Base64 name) {}"));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(2, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(3, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(4, "'PathParam' cannot be resolved to a type; ")));
      // turn off line numbers
   }

   private void firstTestErrorMarks() throws InterruptedException
   {
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(4)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(6)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(7)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(8)));

      //timeout for parsing text
      Thread.sleep(3000);
      assertTrue(selenium().isElementPresent(
         getCodeErrorMarkLocator(10,
            "'ManyToOne' cannot be resolved to a type; 'Mandatory' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(11, "'Property' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(14, "'POST' cannot be resolved to a type; ")));

      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(15)));

      assertTrue(selenium()
         .isElementPresent(
            getCodeErrorMarkLocator(
               16,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(17, "'Base64' cannot be resolved to a type; ")));

      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(18)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(23)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(31)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(33)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(35)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(37)));
      assertTrue(selenium().isElementPresent(
         getCodeErrorMarkLocator(39, "'ChromatticSession' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(
         getCodeErrorMarkLocator(41, "'ChromatticSession' cannot be resolved to a type; ")));
   }

   private void secondVerificationOfErrorMarks()
   {
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(1, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(2, "'Integer1' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(3, "'POST' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(4, "'PathParam' cannot be resolved to a type; ")));
   }

   // IDE-499: "Recognize error "cannot resolve to a type" within the Groovy Template file in the Code Editor."
   @Test
   public void testTemplateFile() throws Exception
   {
      // Open template file with test content
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEMPLATE_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEMPLATE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEMPLATE_FILE_NAME);

      // test error marks
      Thread.sleep(3000);
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(6, "'Path' cannot be resolved to a type; ")));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(7)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(8)));

      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(9, "'POST' cannot be resolved to a type; ")));

      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(10, "'Path' cannot be resolved to a type; ")));

      assertTrue(selenium()
         .isElementPresent(
            getCodeErrorMarkLocator(
               11,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertTrue(selenium().isElementPresent(getCodeErrorMarkLocator(12, "'Base64' cannot be resolved to a type; ")));

      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(13)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(14)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(17)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(19)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(21)));

      // fix error
      selenium()
         .clickAt(
            getCodeErrorMarkLocator(
               11,
               "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; "),
            "");
      IDE.EDITOR.waitTabPresent(1);
      selenium().clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      Thread.sleep(TestConstants.SLEEP);

      // test import statement
      IDE.EDITOR.typeTextIntoEditor(0, Keys.PAGE_UP.toString() + Keys.HOME);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(1).startsWith("<%\n" + "  import java.util.prefs.Base64\n" + "%>\n"));

      // test code error marks
      assertTrue(selenium().isElementPresent(
         getCodeErrorMarkLocator(14,
            "'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ")));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(15)));
      assertFalse(selenium().isElementPresent(getCodeErrorMarkLocator(23)));
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
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
