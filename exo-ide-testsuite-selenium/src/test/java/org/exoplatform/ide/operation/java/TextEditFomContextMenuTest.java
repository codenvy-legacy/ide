package org.exoplatform.ide.operation.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor(0);
      IDE.CONTEXT_MENU.wait();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.SELECT_ALL);
      IDE.CONTEXT_MENU.waitClosed();

      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor(0);
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.DELETE);
      IDE.CONTEXT_MENU.waitClosed();

      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).isEmpty());

      IDE.JAVAEDITOR.callContextMenuIntoJavaEditor(0);
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.UNDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).startsWith("package sumcontroller;"));
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("String one =\"\";"));

      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.REDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).isEmpty());

   }

}
