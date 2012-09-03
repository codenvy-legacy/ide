package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class FormatJavaCodeWithShortKeysTest extends ServicesJavaTextFuctionTest
{
   private static final String PROJECT = FormatJavaCodeWithShortKeysTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/FormatTextTest.zip";

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
   public void formatJavaCodeWithKeys() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      String initialText = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString()+Keys.SHIFT.toString()+"f");
      //need for format text
      Thread.sleep(500);

      //After applying format function to java editor this method should be rewrite.
      //We should compare all text after formatting
      assertFalse(initialText.equals(IDE.JAVAEDITOR.getTextFromJavaEditor(0)));

   }
   
   
}
