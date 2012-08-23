package org.exoplatform.ide.operation.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class FindReplaceFromEditMenu extends ServicesJavaTextFuction
{

   private static final String PROJECT = FindReplaceFromEditMenu.class.getSimpleName();

   final String pathForReopenTestFile = PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
      + "SimpleSum.java";

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
   public void findAndReplaceTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openSpringJavaTetsFile(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("int ss = sumForEdit (c, d);");
      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceButtonEnabled());

      IDE.FINDREPLACE.typeInReplaceField("int newVar = sumForEdit (c, d);");
      IDE.FINDREPLACE.clickReplaceButton();
      IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("int newVar = sumForEdit (c, d);");
      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void findAndReplaceAllWitNoneCaseSensitive() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(pathForReopenTestFile);
      IDE.EDITOR.waitActiveFile(pathForReopenTestFile);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE);

      IDE.FINDREPLACE.waitOpened();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("one");
      IDE.FINDREPLACE.typeInReplaceField("replace");
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());

      IDE.FINDREPLACE.clickReplaceAllButton();
      IDE.JAVAEDITOR.waitErrorLabel("Duplicate field SimpleSum.replace");
      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void findAndReplaceWithNoneCaseSensitive() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(pathForReopenTestFile);
      IDE.EDITOR.waitActiveFile(pathForReopenTestFile);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("ONE");

      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      IDE.FINDREPLACE.clickFindButton();
      assertFalse(IDE.FINDREPLACE.getFindResultText().equals("String not found."));

      IDE.FINDREPLACE.clickFindButton();
      assertTrue(IDE.FINDREPLACE.getFindResultText().equals("String not found."));

      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void replaseAllWithCaseSensitive() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(pathForReopenTestFile);
      IDE.EDITOR.waitActiveFile(pathForReopenTestFile);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.clickCaseSensitiveField();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("ONE");
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      IDE.FINDREPLACE.clickReplaceAllButton();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("String replace")
         && IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("String one"));
      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void replaseAndFindWithCaseSensitive() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(pathForReopenTestFile);
      IDE.EDITOR.waitActiveFile(pathForReopenTestFile);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FIND_REPLACE);
      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.clickCaseSensitiveField();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("ONE");
      IDE.FINDREPLACE.clickFindButton();
      assertTrue(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      IDE.FINDREPLACE.clickReplaceFindButton();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("String replace")
         && IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("String one"));
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
   }

   @Test
   public void findWithShortKey() throws Exception
   {
      IDE.PROJECT.EXPLORER.openItem(pathForReopenTestFile);
      IDE.EDITOR.waitActiveFile(pathForReopenTestFile);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "f");

      IDE.FINDREPLACE.waitOpened();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("int ss = sumForEdit (c, d);");
      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceButtonEnabled());

      IDE.FINDREPLACE.typeInReplaceField("int newVar = sumForEdit (c, d);");
      IDE.FINDREPLACE.clickReplaceButton();
      IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("int newVar = sumForEdit (c, d);");
      IDE.EDITOR.closeTabIgnoringChanges(1);
      
   }

}
