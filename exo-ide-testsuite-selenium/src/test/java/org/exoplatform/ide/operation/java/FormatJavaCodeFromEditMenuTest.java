package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class FormatJavaCodeFromEditMenuTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = FormatJavaCodeFromEditMenuTest.class.getSimpleName();

   private static final String TXT_FILE_NAME = "TestTextFile.txt";

   private static final String JAVA_FILE_NAME = "SimpleSum.java";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/FormatTextTest.zip";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void formatJavaCodeFromEditMenuTest() throws Exception
   {

      final String codeAfterEdit =
         "package sumcontroller;\n\npublic class SimpleSum\n{\n   int c = 225;\n\n   int d = 1;\n\n   public int sumForEdit(int a, int b)\n   {\n      return a + b;\n   }\n\n   int ss = sumForEdit(c, d);\n\n   //For Find/replase test\n   String ONE = \"\";\n\n   String one = \"\";\n}";

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);

      //formating code.
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(JAVA_FILE_NAME);

      //copy formated code
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "a");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "c");

      IDE.EDITOR.closeFile(JAVA_FILE_NAME);

      // create and paste formated code in new txt file to validate formating.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "v");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.setValue(TXT_FILE_NAME);
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitClosed();
      IDE.EDITOR.waitNoContentModificationMark(JAVA_FILE_NAME);

      //comparing
      assertEquals(IDE.EDITOR.getTextFromCodeEditor(), codeAfterEdit);
   }
}
