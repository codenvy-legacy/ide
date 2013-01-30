/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.operation.java.refactoing;

import java.io.IOException;
import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class RefactoringClassFromPackageMenuTest extends RefactService
{

   private static final String PROJECT = RefactoringClassFromPackageMenuTest.class.getSimpleName();

   protected static Map<String, Link> project;

   @BeforeClass
   public static void before()
   {

      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/operation/java/RefactoringTest.zip");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   @AfterClass
   public static void tearDown() throws IOException, InterruptedException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

   @Test
   public void changeClassFromPackageFromMenuInOpenedFiles() throws Exception
   {
      //Open project and rename base class
      openRefacrProject(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("GreetingController.java");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      IDE.REFACTORING.waitRenameForm();
      IDE.REFACTORING.typeNewName("GreetingControllerRefact");
      IDE.REFACTORING.clickRenameButton();
      IDE.REFACTORING.waitRenameFormIsClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingControllerRefact.java");
      // check changes in extended class
      IDE.JAVAEDITOR.selectTab("RefactMethods.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class RefactMethods extends GreetingControllerRefact");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "s");
      IDE.JAVAEDITOR.waitNoContentModificationMark("RefactMethods.java");

      IDE.JAVAEDITOR.selectTab("GreetingControllerRefact.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class GreetingControllerRefact implements Controller");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + "s");
      IDE.JAVAEDITOR.waitNoContentModificationMark("GreetingControllerRefact.java");
      IDE.LOADER.waitClosed();
   }

   @Test
   public void changeClassFromPackageFromContextMenuInOpenedFiles() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("GreetingControllerRefact.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer("GreetingControllerRefact.java");
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.File.RENAME);
      IDE.CONTEXT_MENU.waitClosed();
      IDE.REFACTORING.waitRenameForm();
      IDE.REFACTORING.typeNewName("GreetingController");
      IDE.REFACTORING.clickRenameButton();
      IDE.REFACTORING.waitRenameFormIsClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");

      IDE.JAVAEDITOR.selectTab("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class GreetingController implements Controller");

      IDE.JAVAEDITOR.selectTab("RefactMethods.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class RefactMethods extends GreetingController");

      //for next test
      IDE.EDITOR.forcedClosureFile(1);
      IDE.EDITOR.forcedClosureFile(1);

   }

   @Test
   public void changeClassFromEditMenuInClosedFiles() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("GreetingController.java");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      IDE.REFACTORING.waitRenameForm();
      IDE.REFACTORING.typeNewName("GreetingControllerRefact");
      IDE.REFACTORING.clickRenameButton();
      IDE.REFACTORING.waitRenameFormIsClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingControllerRefact.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingControllerRefact.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class GreetingControllerRefact implements Controller");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("RefactMethods.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("RefactMethods.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class RefactMethods extends GreetingControllerRefact");

      //for next test
      IDE.EDITOR.forcedClosureFile(1);
      IDE.EDITOR.forcedClosureFile(1);
   }

   @Test
   public void changeClassFromContextMenuInClosedFiles() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("GreetingControllerRefact.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openContextMenuOnSelectedItemInPackageExplorer("GreetingControllerRefact.java");
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.File.RENAME);
      IDE.CONTEXT_MENU.waitClosed();
      IDE.REFACTORING.waitRenameForm();
      IDE.REFACTORING.typeNewName("GreetingController");
      IDE.REFACTORING.clickRenameButton();
      IDE.REFACTORING.waitRenameFormIsClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class GreetingController implements Controller");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("RefactMethods.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("RefactMethods.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.JAVAEDITOR.waitIntoJavaEditorSpecifiedText("public class RefactMethods extends GreetingController");
   }

}
