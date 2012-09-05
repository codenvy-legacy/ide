package org.exoplatform.ide.operation.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class FindReplaceFromEditMenuTest extends ServicesJavaTextFuctionTest
{

   private static final String PROJECT = FindReplaceFromEditMenuTest.class.getSimpleName();

   final String pathForReopenTestFile = PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
      + "SimpleSum.txt";

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

   @After
   public final void closeTab()
   {
      try
      {
         IDE.EDITOR.forcedClosureFile(1);
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
      openJavaClassForFormat(PROJECT);
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
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());

      IDE.FINDREPLACE.clickFindButton();
      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceButtonEnabled());
      IDE.FINDREPLACE.typeInReplaceField("int newVar = sumForEdit (c, d);");
      IDE.FINDREPLACE.clickReplaceButton();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("int newVar = sumForEdit (c, d);"));

   }

   @Test
   public void findAndReplaceAllWitNoneCaseSensitive() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

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

   }

   @Test
   public void findAndReplaceWithNoneCaseSensitive() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
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
      assertFalse(IDE.FINDREPLACE.getFindResultText().equals("String not found."));

   }

   @Test
   public void replaseAllWithCaseSensitive() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

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
      IDE.FINDREPLACE.typeInReplaceField("replace");
      IDE.FINDREPLACE.clickReplaceAllButton();

      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("String replace")
         && IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("String one"));

   }

   @Test
   public void replaseAndFindWithCaseSensitive() throws Exception
   {

      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
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
      IDE.FINDREPLACE.typeInReplaceField("replace");
      IDE.FINDREPLACE.clickReplaceFindButton();

      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("String replace")
         && IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("String one"));
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
   }

   @Test
   public void findWithShortKey() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      openJavaClassForFormat(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "f");
      //IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() + "f");

      IDE.FINDREPLACE.waitOpened();
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.FIND_REPLACE));

      IDE.FINDREPLACE.typeInFindField("int ss = sumForEdit (c, d);");
      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertFalse(IDE.FINDREPLACE.isReplaceButtonEnabled());
      IDE.FINDREPLACE.clickFindButton();
      assertTrue(IDE.FINDREPLACE.isFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceAllButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceFindButtonEnabled());
      assertTrue(IDE.FINDREPLACE.isReplaceButtonEnabled());
      IDE.FINDREPLACE.typeInReplaceField("int newVar = sumForEdit (c, d);");
      IDE.FINDREPLACE.clickReplaceButton();

      IDE.JAVAEDITOR.getTextFromJavaEditor(0).equals("int newVar = sumForEdit (c, d);");

   }

}
