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
import org.exoplatform.ide.core.Outline.TokenType;
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
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineChromatticTest()
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
            MimeType.CHROMATTIC_DATA_OBJECT,
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
   
   @Test
   public void testCodeOutLineChromattic() throws Exception
   {     
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      
      // check for presence and visibility of outline tab
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.OUTLINE.isOutlineViewVisible());
      
      // create initial outline tree map
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("@\nDataObject", 7, TokenType.CLASS, "DataObject");
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
      
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();
      
      // create opened outline tree map
      outlineTreeHelper.clearOutlineTreeInfo();      
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("@\nDataObject", 7, TokenType.CLASS, "DataObject");
      outlineTreeHelper.addOutlineItem("@a : java.lang.String", 9, TokenType.PROPERTY, "a");
      outlineTreeHelper.addOutlineItem("@b : String", 10, TokenType.PROPERTY, "b");
      outlineTreeHelper.addOutlineItem("hello(int) : void", 13, TokenType.METHOD, "hello");
      outlineTreeHelper.addOutlineItem("@product : Product", 15, TokenType.VARIABLE, "product");
      outlineTreeHelper.addOutlineItem("@quantity : int", 21, TokenType.PROPERTY, "quantity");
      outlineTreeHelper.addOutlineItem("getValue() : void", 23, TokenType.METHOD, "getValue");
      outlineTreeHelper.addOutlineItem("c1 : Object", 24, TokenType.VARIABLE, "c1");
      outlineTreeHelper.addOutlineItem("c2 : Object", 37, false, TokenType.VARIABLE, "c2");  // false, because outline node is not highlighted from test, but highlighted when goto this line manually
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

}
