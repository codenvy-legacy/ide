package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TextEditFomContextMenuTest extends ServicesJavaTextFuction
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
   public void selectAllDleteUndoRedoTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor();
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.SELECT_ALL);
      IDE.CONTEXT_MENU.waitClosed();

      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor();
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.DELETE);
      IDE.CONTEXT_MENU.waitClosed();

      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().isEmpty());

      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor();
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.UNDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().startsWith("package sumcontroller;"));
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("String one =\"\";"));

      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor();
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.REDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().isEmpty());

   }

}
