package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class SelectAllFromEditMenuTest extends ServicesJavaTextFuction
{

   private static final String PROJECT = SelectAllFromEditMenuTest.class.getSimpleName();

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
   public void selectAllTesUi() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);
      IDE.JAVAEDITOR.setCursorToJavaEditor();

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SELECT_ALL);
      // need for setting selection area
      Thread.sleep(500);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.BACK_SPACE.toString());
      // need for reparce in java editor
      Thread.sleep(500);
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().isEmpty());

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);

      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains(
         "public class SumController extends AbstractController {"));
   }

}
