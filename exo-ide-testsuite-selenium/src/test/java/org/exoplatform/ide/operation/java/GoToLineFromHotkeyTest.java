package org.exoplatform.ide.operation.java;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "l");
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.goToLine(5);
      IDE.STATUSBAR.waitCursorPositionAt("5 : 1");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.ARROW_RIGHT.toString());
      IDE.STATUSBAR.waitCursorPositionAt("5 : 2");
   }

}
