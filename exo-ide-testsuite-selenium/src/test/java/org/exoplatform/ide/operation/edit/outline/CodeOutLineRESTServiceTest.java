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

import org.everrest.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Oct 26, 2010 $
 *
 */
public class CodeOutLineRESTServiceTest extends BaseTest
{

   private final static String FILE_NAME = "RESTCodeOutline.groovy";

   private final static String FOLDER = CodeOutLineRESTServiceTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineRESTServiceTest()
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
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, "exo:groovyResourceContainer", URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @After
   public void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      IDE.EDITOR.closeFile(0);
   }

   //TODO Issue IDE - 486    
   //TODO Issue IDE - 466 
   @Test
   public void testCodeOutLineRestService() throws Exception
   {
      // Open REST service file with content
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL+FILE_NAME);   
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);
      
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      
      // check for presence and visibility of outline tab
      IDE.OUTLINE.assertOutlineTreePresent();
      IDE.OUTLINE.checkOutlinePanelVisibility(true);

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