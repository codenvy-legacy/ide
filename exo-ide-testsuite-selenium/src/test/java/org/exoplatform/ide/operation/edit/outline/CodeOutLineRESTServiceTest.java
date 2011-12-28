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

import static org.junit.Assert.assertTrue;

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

   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineRESTServiceTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineChromatticTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.GROOVY_SERVICE,
            "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
   }

   //TODO Issue IDE - 486    
   //TODO Issue IDE - 466 
   @Test
   public void testCodeOutLineRestService() throws Exception
   {     
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      
      // check for presence and visibility of outline tab
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.OUTLINE.isOutlineViewVisible());
      
      // create initial outline tree map
      outlineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("@TestService", 6, false);
      outlineTreeHelper.addOutlineItem("Dep", 32, false);

      // check is tree created correctly
      outlineTreeHelper.checkOutlineTree();
      
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // create opened outline tree map
      outlineTreeHelper.clearOutlineTreeInfo();
      outlineTreeHelper.init();      
      outlineTreeHelper.addOutlineItem("@TestService", 6);
      outlineTreeHelper.addOutlineItem("@post1(@String, @String, @String, String) : String", 12);
      outlineTreeHelper.addOutlineItem("@post2(@String, @java.lang.String, @String, java.lang.String) : java.lang.String", 24);
      outlineTreeHelper.addOutlineItem("Dep", 32);
      outlineTreeHelper.addOutlineItem("name : String", 34);
      outlineTreeHelper.addOutlineItem("age : int", 35);
      outlineTreeHelper.addOutlineItem("addYear() : void", 37);
      outlineTreeHelper.addOutlineItem("greet(@String) : java.lang.String", 39);
      outlineTreeHelper.addOutlineItem("address : int", 42, false);   // false, because outline node is not highlighted from test, but highlighted when goto this line manually

      // check is tree created correctly
      outlineTreeHelper.checkOutlineTree();
   }
}