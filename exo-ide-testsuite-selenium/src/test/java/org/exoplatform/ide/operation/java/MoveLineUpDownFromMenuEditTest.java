package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MoveLineUpDownFromMenuEditTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = MoveLineUpDownFromMenuEditTest.class.getSimpleName();

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
   public void moveLineUpDownWithKeys() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.GOTOLINE.goToLine(19);
      IDE.STATUSBAR.waitCursorPositionAt("19 : 1");

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.MOVE_LINE_UP);
      Thread.sleep(500);
      assertEquals("mav.addObject(\"x\", x);", IDE.JAVAEDITOR.getTextFromSetPosition(21).trim());

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.MOVE_LINE_DOWN);
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      assertEquals("mav.addObject(\"y\", y);", IDE.JAVAEDITOR.getTextFromSetPosition(26).trim());
   }
}
