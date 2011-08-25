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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineGroovyTest extends BaseTest
{
   private final static String FILE_NAME = "GroovyCodeOutline.groovy";

   private final static String FOLDER = CodeOutLineGroovyTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER + "/";

   private OulineTreeHelper outlineTreeHelper;

   public CodeOutLineGroovyTest()
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
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_GROOVY, "exo:groovyResourceContainer", URL
            + FILE_NAME);
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

   @Test
   public void testCodeOutLineGroovy() throws Exception
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

      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // create outline tree map
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("TestJSON", 6);
      outlineTreeHelper.addOutlineItem("a1 : java.lang.A", 7);
      outlineTreeHelper.addOutlineItem("a2 : java.lang.A", 7, false);
      outlineTreeHelper.addOutlineItem("a3 : java.lang.A", 7, false);
      outlineTreeHelper.addOutlineItem("b1 : java.lang.B", 8);
      outlineTreeHelper.addOutlineItem("b2 : java.lang.B", 8, false);
      outlineTreeHelper.addOutlineItem("b3 : String", 8, false);
      outlineTreeHelper.addOutlineItem("getValue1() : java.lang.String", 10);
      outlineTreeHelper.addOutlineItem("c1 : String", 11);
      outlineTreeHelper.addOutlineItem("identity : Identity", 19);
      outlineTreeHelper.addOutlineItem("c2 : String", 27);
      outlineTreeHelper.addOutlineItem("d : java.lang.String", 31);
      outlineTreeHelper.addOutlineItem("setValue2(@String) : void", 33);
      outlineTreeHelper.addOutlineItem("printClosureInner : Object", 35, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("printClosureOuter() : Object", 40, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("hello() : Object", 42);
      outlineTreeHelper.addOutlineItem("name4 : String", 44);
      outlineTreeHelper.addOutlineItem("g : String", 47, false, TokenType.PROPERTY); // false, because outline node is not highlighted from test, but highlighted when goto this line manually
      outlineTreeHelper.addOutlineItem("@get(@java.lang.List<? extends Tree>) : Collection<HashMap<String,String>>", 53, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("col : Object", 54, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("var1 : List<String>", 55, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("products : Object", 59, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("row : Object", 61, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("var2 : List<String>", 66, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("@add(HashMap<String,String>) : HashMap<String,String>", 75, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("addVar1 : List<Tree>", 78, false, TokenType.VARIABLE); // false, because outline node is not highlighted from test, but highlighted when goto this line manually

      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

   @After
   public void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + FOLDER);
      IDE.EDITOR.closeFile(0);
   }
}
