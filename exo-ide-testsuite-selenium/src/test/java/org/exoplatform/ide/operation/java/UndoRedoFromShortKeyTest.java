package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class UndoRedoFromShortKeyTest extends ServicesJavaTextFuctionTest
{

   private static final String PROJECT = UndoRedoFromShortKeyTest.class.getSimpleName();

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
   public void undRedoFromKeys() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);
      

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "\n");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "//type1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "s");
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, " //type2");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "s");
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1 //type2"));

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "z");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "z");
      //need for reparse on staging
      Thread.sleep(2000);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1 //type2"));
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "y");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "y");
      Thread.sleep(2000);
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1 //type2"));
      //set file in the initial state
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "z");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "z");
      Thread.sleep(2000);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1 //type2"));

   }

}
