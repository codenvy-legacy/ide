package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class DeleteLineFromEditMenu extends ServicesJavaTextFuction
{
   private static final String PROJECT = DeleteLineFromEditMenu.class.getSimpleName();

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
   public void deleteCurrentLineUi() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile (PROJECT);
      waitEditorIsReady(PROJECT);
      
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.goToLine(1);
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      String code = IDE.EDITOR.getTextFromCodeEditor(0);
      assertFalse(code.contains("package sumcontroller;"));

      //return file to initial state
      //timeout need for reparse new state in code editor 
      Thread.sleep(500);
      //call undo command and check begining state
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "z");
      assertTrue(code.contains("package sumcontroller;"));
   }
}
