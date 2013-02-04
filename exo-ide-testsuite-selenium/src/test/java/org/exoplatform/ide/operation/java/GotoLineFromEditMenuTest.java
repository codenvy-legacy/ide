package org.exoplatform.ide.operation.java;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class GotoLineFromEditMenuTest extends ServicesJavaTextFuction
{

   private static final String PROJECT = GotoLineFromEditMenuTest.class.getSimpleName();

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
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      openSpringJavaTetsFile(PROJECT);

      IDE.GOTOLINE.goToLine(15);
      IDE.STATUSBAR.waitCursorPositionAt("15 : 1");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ARROW_RIGHT.toString());
      IDE.STATUSBAR.waitCursorPositionAt("15 : 2");
   }

}
