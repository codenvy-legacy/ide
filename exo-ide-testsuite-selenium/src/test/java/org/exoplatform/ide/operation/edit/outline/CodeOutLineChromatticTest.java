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

import static org.junit.Assert.assertEquals;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:
 * 
 */
public class CodeOutLineChromatticTest extends CodeAssistantBaseTest
{
   private final static String FILE_NAME = "ChromatticOutline.cmtc";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineChromatticTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.CHROMATTIC_DATA_OBJECT, "src/test/resources/org/exoplatform/ide/operation/edit/outline/"
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
      //TODO Pause for build outline tree
      //after implementation method for check ready state, should be remove
      Thread.sleep(4000);
   }

   @Test
   public void testCodeOutLineChromattic() throws Exception
   {
      // step 1 open outline panel and valid first node
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      IDE.OUTLINE.waitNodeWithSubNamePresent("DataObject");

      // step 2 o to line #14 end check collapsed nodes
      IDE.GOTOLINE.goToLine(14);
      IDE.OUTLINE.waitNodeWithSubNamePresent("getValue");
      assertEquals(IDE.OUTLINE.getItemLabel(2), "@a : java.lang.String");
      assertEquals(IDE.OUTLINE.getItemLabel(3), "@b : String");
      assertEquals(IDE.OUTLINE.getItemLabel(4), "hello(int) : void");
      assertEquals(IDE.OUTLINE.getItemLabel(5), "@quantity : int");

      // Gstep 3 go to line #20 end check collapsed nodes 'hello'
      IDE.GOTOLINE.goToLine(20);
      IDE.OUTLINE.waitElementIsSelect("@product : Product");
      assertEquals(IDE.OUTLINE.getItemLabel(6), "@quantity : int");

      // Gstep 4 go to line #29 last node
      IDE.GOTOLINE.goToLine(29);
      IDE.OUTLINE.waitElementIsSelect("c1 : Object");
      IDE.OUTLINE.waitItemAtPosition("c2 : Object", 9);

   }
}