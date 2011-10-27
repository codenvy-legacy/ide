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
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a> 
 * @version $Id:
 *
 */
public class CodeOutLineChromatticTest extends BaseTest
{
   private final static String FILE_NAME = "ChromatticOutline.groovy";

   private final static String FOLDER = CodeOutLineChromatticTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineChromatticTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }
   
   @Before
   public void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.CHROMATTIC_DATA_OBJECT, "nt:resource", URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCodeOutLineChromattic() throws Exception
   {
      // Open groovy file with content
      IDE.WORKSPACE.waitForRootItem();
      
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);
      
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      
      // check for presence and visibility of outline tab
      IDE.OUTLINE.assertOutlineTreePresent();
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
      
      // create initial outline tree map
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("@DataObject", 7, TokenType.CLASS, "DataObject");
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
      
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();
      
      // create opened outline tree map
      outlineTreeHelper.clearOutlineTreeInfo();      
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("@DataObject", 7, TokenType.CLASS, "DataObject");
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

   @After
   public void tearDown() throws Exception
   {
     IDE.EDITOR.closeFile(0);
     VirtualFileSystemUtils.delete(WS_URL + FOLDER);
   }
}
