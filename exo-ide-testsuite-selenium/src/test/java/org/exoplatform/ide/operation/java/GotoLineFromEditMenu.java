package org.exoplatform.ide.operation.java;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class GotoLineFromEditMenu extends ServicesJavaTextFuction
{

   private static final String PROJECT = GotoLineFromEditMenu.class.getSimpleName();

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
   public void checkGoToLineWithUI() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      openSpringJavaTetsFile(PROJECT);
      waitEditorIsReady(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.goToLine(15);
      IDE.STATUSBAR.waitCursorPositionAt("15:1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.ARROW_RIGHT.toString());
      IDE.STATUSBAR.waitCursorPositionAt("15:2");
   }

}
