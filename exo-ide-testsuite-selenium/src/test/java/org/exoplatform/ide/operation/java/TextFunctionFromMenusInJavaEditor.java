package org.exoplatform.ide.operation.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.Map;

public class TextFunctionFromMenusInJavaEditor extends BaseTest
{

   private static final String PROJECT = TextFunctionFromMenusInJavaEditor.class.getSimpleName();

   private static final String PROJECT2 = "txttesting";

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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      openSumController();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.goToLine(15);
      IDE.STATUSBAR.waitCursorPositionAt("15:1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.ARROW_RIGHT.toString());
      IDE.STATUSBAR.waitCursorPositionAt("15:2");
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.goToLine(1);
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
   }

   @Test
   public void deleteCurrentLineAndUndoWithUi() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      waitEditorIsReady();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
      String code = IDE.EDITOR.getTextFromCodeEditor(0);
      assertFalse(code.contains("package sumcontroller;"));
   }

   @Test
   public void undRedoFromUi() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      waitEditorIsReady();
      //go to begin of the code
      IDE.GOTOLINE.goToLine(1);
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
      IDE.STATUSBAR.waitCursorPositionAt("1:1");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "/n");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "//type1");
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      IDE.JAVAEDITOR.setCursorToJavaEditor(0);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "/n");
            IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "//type2");
      IDE.TOOLBAR.runCommand(MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark("SumController.java");
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1"));
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type2"));
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type1"));
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("//type2"));
   }
 //--------------------------------------------------------------------
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

   private void alternativeOpen() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT2);
      IDE.PROJECT.EXPLORER.expandItem(PROJECT2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT2 + "/" + "code.txt");
      IDE.PROJECT.EXPLORER.openItem(PROJECT2 + "/" + "code.txt");
      Thread.sleep(2000);
      IDE.selectMainFrame();
      // IDE.EDITOR.waitActiveFile(PROJECT2 + "/" + "code.txt");
   }

   //   @Test
   //   public void removeBlockCommentByInnerSelection() throws Exception
   //   {
   //     
   //   }
   //
   //   @Test
   //   public void removeBlockCommentByStartSelection() throws Exception
   //   {
   //      
   //   }
   //
   //   @Test
   //   public void removeBlockCommentByEndSelection() throws Exception
   //   {
   //      
   //   }

   private void waitEditorIsReady() throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

}
