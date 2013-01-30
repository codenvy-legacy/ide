/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.edit.outline;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Code Outline panel for javascript file.
 * 
 * That tree is correctly displayed for javascript file.
 * 
 * That if working throught file, than correct node is highlited in outline
 * tree.
 * 
 * That if click on node in outline tree, than cursor goes to this token in
 * editor.
 * 
 * @author <a href="dnochevnovr@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:
 * 
 */

//TODO On this moment javascript outline is not work
public class CodeOutlineJavaScriptTest extends CodeAssistantBaseTest
{

   private final static String FILE_NAME = "TestJavaScriptFile.js";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineChromatticTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_JAVASCRIPT, "src/test/resources/org/exoplatform/ide/operation/edit/outline/"
               + FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @Before
   public void openFile() throws Exception
   {
      openProject();
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();
      Thread.sleep(TestConstants.SLEEP);
   }

   //TODO After fix ISSUE 2155 we should complete this test
   @Test
   public void testCodeOutlineJavaScript() throws Exception
   {
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);

   }
}