package org.exoplatform.ide.operation.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

public class FormatJavaCodeFromEditMenuTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = FormatJavaCodeFromEditMenuTest.class.getSimpleName();

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
   public void selectAllDeleteUndoRedoTest() throws Exception
   {
      final String codeAfterEdit =
         "package sumcontroller;" + "\n" + "   int c = 225;" + "\n" + "      return a + b;" + "\n"
            + "   //For Find/replase test" + "\n" + "}" + "\n" + "public class SimpleSum" + "\n" + "{"
            + "\n" + "   int d = 1;" + "\n" + "   public int sumForEdit(int a, int b)" + "\n" + "   {" + "\n" + "   }"
            + "\n" + "   int ss = sumForEdit(c, d);" + "\n" + "   String ONE = \"\";" + "\n"+"   String one = \"\";";

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      String initialText = IDE.JAVAEDITOR.getTextFromJavaEditor(0);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT);
      //need for format text
      String initialText2 = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
      System.out.println("<<<<<<<<<<<<<<<<<<<<<:" + "\n" + initialText2);
      Thread.sleep(500);

      //After applying format function to java editor this method should be rewrite.
      //We should compare all text after formatting
      assertEquals(IDE.JAVAEDITOR.getTextFromJavaEditor(0), codeAfterEdit);

   }

}
