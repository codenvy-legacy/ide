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
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Oct 26, 2010 $
 * 
 */
public class CodeOutLineRESTServiceTest extends CodeAssistantBaseTest
{

   private final static String FILE_NAME = "RESTCodeOutline.groovy";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineChromatticTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.GROOVY_SERVICE, "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME);
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

      //TODO REMOVE IT WHEN IT WILL BE POSSIBLE
      Thread.sleep(4000);
   }

   @Test
   public void testCodeOutLineRestService() throws Exception
   {
      // open outline panel and initial state nodes in Outline Tree
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      IDE.OUTLINE.waitItemAtPosition("@TestService", 1);
      IDE.OUTLINE.waitItemAtPosition("TestJSON", 2);

      // collapse 2 node and check items
      IDE.GOTOLINE.goToLine(18);
      IDE.OUTLINE.waitElementIsSelect("@post1(TestJSON) : String");
      IDE.OUTLINE.waitItemAtPosition("@post2(String) : String", 3);

      // collapse 'TestJSON' node and check items
      IDE.GOTOLINE.goToLine(30);
      IDE.OUTLINE.waitElementIsSelect("value : String");
      IDE.OUTLINE.waitItemAtPosition("getValue() : String", 6);
      IDE.OUTLINE.waitItemAtPosition("setValue(String) : void", 7);

      IDE.OUTLINE.selectRow(4);
      IDE.STATUSBAR.waitCursorPositionAt("29 : 1");

      IDE.OUTLINE.selectItem("@");
      IDE.STATUSBAR.waitCursorPositionAt("12 : 1");

      IDE.OUTLINE.selectRow(3);
      IDE.STATUSBAR.waitCursorPositionAt("25 : 1");

      IDE.OUTLINE.selectRow(3);
      IDE.STATUSBAR.waitCursorPositionAt("25 : 1");

      IDE.OUTLINE.selectItem("setValue");
      IDE.STATUSBAR.waitCursorPositionAt("37 : 1");

   }
}