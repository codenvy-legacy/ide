package org.exoplatform.ide.operation.java;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class GoToLineFromHotkeyTest extends ServicesJavaTextFuction
{

   private static final String PROJECT = GoToLineFromHotkeyTest.class.getSimpleName();

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
   public void goToLineFromKeys() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "l");
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.typeIntoLineNumberField(String.valueOf(5));
      IDE.GOTOLINE.clickGoButton();
      IDE.GOTOLINE.waitClosed();

      IDE.STATUSBAR.waitCursorPositionAt("5 : 1");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ARROW_RIGHT.toString());
      IDE.STATUSBAR.waitCursorPositionAt("5 : 2");
   }

}
