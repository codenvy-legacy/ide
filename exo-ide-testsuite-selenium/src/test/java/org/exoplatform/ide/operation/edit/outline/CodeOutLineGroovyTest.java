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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineGroovyTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }
   
   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_GROOVY, "exo:groovyResourceContainer", URL + FILE_NAME);
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
      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.clickOpenIconOfFolder(URL);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

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
      outlineTreeHelper.addOutlineItem("printClosureInner", 35);
      outlineTreeHelper.addOutlineItem("printClosureOuter", 40);      
      outlineTreeHelper.addOutlineItem("hello()", 42);
      outlineTreeHelper.addOutlineItem("name4 : String", 44);
      outlineTreeHelper.addOutlineItem("g : String", 47);     
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

   @Ignore      //TODO Issue IDE - 466
   @AfterClass
   public static void tearDown() throws Exception
   {
     IDE.EDITOR.closeTab(0);
      cleanDefaultWorkspace();
   }
}
