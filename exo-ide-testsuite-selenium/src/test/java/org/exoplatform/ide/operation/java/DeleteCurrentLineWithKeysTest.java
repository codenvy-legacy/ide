package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class DeleteCurrentLineWithKeysTest extends ServicesJavaTextFuctionTest
{
   private static final String PROJECT = DeleteCurrentLineWithKeysTest.class.getSimpleName();

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
   public void deleteCurrentLineWithKeys() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);
      waitEditorIsReady(PROJECT);

      IDE.GOTOLINE.goToLine(1);
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "d");
      String code = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
      assertFalse(code.contains("package sumcontroller;"));
   }
}
