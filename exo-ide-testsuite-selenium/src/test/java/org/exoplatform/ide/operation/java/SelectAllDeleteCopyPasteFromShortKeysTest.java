package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

public class SelectAllDeleteCopyPasteFromShortKeysTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = SelectAllDeleteCopyPasteFromShortKeysTest.class.getSimpleName();

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
   public void selectAllTextCutDeleteCopyAndPasteTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.JAVAEDITOR.setCursorToJavaEditor();
      String compare = IDE.JAVAEDITOR.getTextFromJavaEditor();
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "a");
      // need for setting selection area
      Thread.sleep(500);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "x");
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().isEmpty());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "v");
      assertEquals(compare, IDE.JAVAEDITOR.getTextFromJavaEditor());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(" //type for compare");
      Thread.sleep(2000);
      String compareAfterEdit = IDE.JAVAEDITOR.getTextFromJavaEditor();

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "a");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "c");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.DELETE.toString());
      Thread.sleep(2000);
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().isEmpty());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "v");

      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      // need for reparse in java editor
      Thread.sleep(5000);
      assertEquals(compareAfterEdit, IDE.JAVAEDITOR.getTextFromJavaEditor());
   }

}
