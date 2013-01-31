package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class MoveLineUpDownWithShortKeysTest extends ServicesJavaTextFuction
{

   private static final String PROJECT = MoveLineUpDownWithShortKeysTest.class.getSimpleName();

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
   public void moveLineUpDown() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.GOTOLINE.goToLine(19);
      IDE.STATUSBAR.waitCursorPositionAt("19 : 1");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ALT.toString() + Keys.ARROW_UP.toString());
      assertEquals("mav.addObject(\"x\", x);", IDE.JAVAEDITOR.getTextFromSetPosition(21).trim());

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ALT.toString() + Keys.ARROW_DOWN.toString());
      assertEquals("mav.addObject(\"y\", y);", IDE.JAVAEDITOR.getTextFromSetPosition(26).trim());
   }

}
