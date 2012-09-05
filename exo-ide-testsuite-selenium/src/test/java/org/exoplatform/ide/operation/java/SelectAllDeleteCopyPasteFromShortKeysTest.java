package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class SelectAllDeleteCopyPasteFromShortKeysTest extends ServicesJavaTextFuctionTest
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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);

      IDE.JAVAEDITOR.setCursorToJavaEditor(0);
      String compare = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "a");
      //need for setting selection area
      Thread.sleep(500);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "x");
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).isEmpty());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "v");
      assertEquals(compare, IDE.JAVAEDITOR.getTextFromJavaEditor(0));
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, " //type for compare");
      String compareAfterEdit = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "a");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "c");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.DELETE.toString());
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).isEmpty());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "v");

      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      //need for reparse in java editor
      Thread.sleep(5000);
      assertEquals(compareAfterEdit, IDE.JAVAEDITOR.getTextFromJavaEditor(0));
   }

}
