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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
 * @version $Id: 4:57:57 PM  Jan 29, 2013 $
 *
 */
public class RefactoringRenameSharedVariableTest extends RefactService
{
   private static final String PROJECT = RefactoringRenameSharedVariableTest.class.getSimpleName();

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
   public void renameSharedVariableTest() throws Exception
   {
      openRefacrProject(PROJECT);
      assertFalse(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("newNameOfTheVar"));

      IDE.EDITOR.selectTab("GreetingController.java");

      IDE.GOTOLINE.goToLine(15);
      IDE.JAVAEDITOR.moveCursorRight(18);

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ALT.toString() + Keys.SHIFT.toString() + "r");

      IDE.REFACTORING.waitRenameForm();
      IDE.REFACTORING.typeNewName("newNameOfTheVar");
      IDE.REFACTORING.clickRenameButton();
      IDE.LOADER.waitClosed();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("public String newNameOfTheVar"));

      IDE.EDITOR.selectTab("RefactMethods.java");

      // checking that variable changed in child class
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("newNameOfTheVar"));

   }

}
