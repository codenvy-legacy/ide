package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class TextFunctionsFromKeysInJavaEditor extends BaseTest
{

   private static final String PROJECT = TextFunctionsFromKeysInJavaEditor.class.getSimpleName();

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
      openSumController();
      waitEditorIsReady();
//      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "d");
//      String code = IDE.JAVAEDITOR.getTextFromJavaEditor(0);
//      assertFalse(code.contains("package sumcontroller;"));
   }

   @Test
   public void undRedoFromKeys() throws Exception
   {
      waitEditorIsReady();
      IDE.JAVAEDITOR.setCursorToJavaEditor(0);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "\n");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "//type1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "s");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "s");
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      Thread.sleep(15000);
      //      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      //      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      //      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1"));
      //      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type2"));
      //      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      //      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      //      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1"));
      //      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type2"));
   }

   private void waitEditorIsReady() throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   private void openSumController() throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      waitEditorIsReady();
   }

}
