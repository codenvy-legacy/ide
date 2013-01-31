package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class UndoRedoFromEditMenuTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = UndoRedoFromEditMenuTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {

      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/calc.zip";

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
   public void undoRedoFromUi() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\n");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("//type1");
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(" //type2");
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("//type1 //type2"));
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("//type1 //type2"));
   }

}
