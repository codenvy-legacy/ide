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

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 6:00:38 PM  Jan 29, 2013 $
 *
 */
public class RefactoringRenameCheckingPopupMessagesTest extends RefactService
{
   private static final String PROJECT = RefactoringRenameCheckingPopupMessagesTest.class.getSimpleName();

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
   public void checkRefactoringPopupNotificationsTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      // step 1 Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();

      IDE.GOTOLINE.goToLine(15);

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ALT.toString() + Keys.SHIFT.toString() + "r");
      IDE.REFACTORING.waitPopupWithWaitInitializeJavaToolingMessage();
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.JAVAEDITOR.selectTab("GreetingController.java");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("blablabla");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ALT.toString() + Keys.SHIFT.toString() + "r");
      IDE.REFACTORING.waitPopupWithMessageThatNeedSaveFileBeforeRefactor();
      IDE.INFORMATION_DIALOG.clickOk();
   }
}