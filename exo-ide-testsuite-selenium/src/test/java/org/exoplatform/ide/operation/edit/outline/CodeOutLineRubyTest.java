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

import java.io.IOException;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineRubyTest extends BaseTest
{
   private final static String FILE_NAME = "TestRubyFile.rb";

   private final static String PROJECT_NAME = CodeOutLineRubyTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT_NAME);
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_RUBY, "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT_NAME);
      }
      catch (IOException e)
      {

      }
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();
      //TODO After add progressor to Ruby file delay should be remove
      Thread.sleep(4000);
   }

   @Test
   public void testCodeOutLineRuby() throws Exception
   {
      // step 1 open file, open outline panel, check initial state of the ruby outline tree 
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      IDE.OUTLINE.waitNodeWithSubNamePresent("TestModule");

      // step 2 expand class node and check items
      IDE.GOTOLINE.goToLine(20);
      checkNodesWithNameClass();

      //step3 expand node TC_Test 
      IDE.GOTOLINE.goToLine(60);
      checkNodeWithTC_TestName();

      //step4 expand node TestModule 
      IDE.GOTOLINE.goToLine(72);
      checkModeWithNameTestModule();

      //step 5 navigate on outline tree and check items
      IDE.OUTLINE.selectItem("CLASS_CONSTANT");
      IDE.STATUSBAR.waitCursorPositionAt("20 : 1");

      IDE.OUTLINE.selectItem("TOPLEVEL_CONSTANT");
      IDE.STATUSBAR.waitCursorPositionAt("5 : 1");

      IDE.OUTLINE.selectItem("$myFile");
      IDE.STATUSBAR.waitCursorPositionAt("33 : 1");

      IDE.OUTLINE.selectItem("TC_MyTest");
      IDE.STATUSBAR.waitCursorPositionAt("58 : 1");

      IDE.OUTLINE.selectItem("TestModule");
      IDE.STATUSBAR.waitCursorPositionAt("71 : 1");

      IDE.OUTLINE.selectItem("ascii1");
      IDE.STATUSBAR.waitCursorPositionAt("77 : 1");

   }

   /**
    * check main items in expanded node with name 'Module'
    * @throws Exception
    */
   private void checkModeWithNameTestModule() throws Exception
   {
      IDE.OUTLINE.waitElementIsSelect("method()");
      IDE.OUTLINE.waitNodeWithSubNamePresent("method()");
      IDE.OUTLINE.waitNodeWithSubNamePresent("A");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Symbol");
      IDE.OUTLINE.waitNodeWithSubNamePresent("ascii1");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Ascii");
   }

   /**
    * check main items in expanded node with name 'TC_TestName'
    * @throws Exception
    */
   private void checkNodeWithTC_TestName() throws Exception
   {
      IDE.OUTLINE.waitElementIsSelect("foo()");
      IDE.OUTLINE.waitNodeWithSubNamePresent("foo()");
      IDE.OUTLINE.waitNodeWithSubNamePresent("@@class_variable");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Hash");
      IDE.OUTLINE.waitNodeWithSubNamePresent("@field");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Number");
      IDE.OUTLINE.waitNodeWithSubNamePresent("a");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Fixnum");
   }

   /**
    * check main items in expanded node with name 'Class'
    * @throws Exception
    */
   private void checkNodesWithNameClass() throws Exception
   {
      IDE.OUTLINE.waitElementIsSelect("CLASS_CONSTANT : Fixnum");
      IDE.OUTLINE.waitNodeWithSubNamePresent("CLASS_CONSTANT");
      IDE.OUTLINE.waitNodeWithSubNamePresent("@field");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Object");
      IDE.OUTLINE.waitNodeWithSubNamePresent("@@char");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Ascii");
      IDE.OUTLINE.waitNodeWithSubNamePresent("@@n2");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Float");
   }

}
