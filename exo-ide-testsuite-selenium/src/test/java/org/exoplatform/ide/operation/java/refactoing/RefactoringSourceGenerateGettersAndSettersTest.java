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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 12:11:37 PM  Jan 30, 2013 $
 *
 */
public class RefactoringSourceGenerateGettersAndSettersTest extends RefactService
{
   private static final String PROJECT = RefactoringSourceGenerateGettersAndSettersTest.class.getSimpleName();

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
   public void generateGettersAndSettersPopupNotificationsTest() throws Exception
   {
      openOneJavaClassForRefactoring(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SOURCE,
         MenuCommands.Source.GENERATE_GETTERS_AND_SETTERS);
      IDE.WARNING_DIALOG.waitOpened();
      IDE.WARNING_DIALOG.getWarningMessage();
      IDE.WARNING_DIALOG.clickOk();

      IDE.GOTOLINE.goToLine(21);
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SOURCE,
         MenuCommands.Source.GENERATE_GETTERS_AND_SETTERS);
      IDE.REFACTORING.waitGenerateGettersAndSettersForm();
      IDE.REFACTORING.clickOnClassFieldWithName("bidVal");
      IDE.REFACTORING.clickOkGettersAndSettersForm();
      IDE.EDITOR.waitFileContentModificationMark("GreetingController.java");

      String resultCode = IDE.JAVAEDITOR.getTextFromJavaEditor();

      assertTrue(resultCode.contains("* @return the bidVal"));
      assertTrue(resultCode.contains("public double getBidVal()"));
      assertTrue(resultCode.contains("return bidVal;"));

      assertTrue(resultCode.contains("* @param bidVal the bidVal to set"));
      assertTrue(resultCode.contains("public void setBidVal(double bidVal)"));
      assertTrue(resultCode.contains("this.bidVal = bidVal;"));
   }
}
