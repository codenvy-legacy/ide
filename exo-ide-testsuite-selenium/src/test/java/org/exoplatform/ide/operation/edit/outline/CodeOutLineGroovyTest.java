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
import static org.junit.Assert.assertTrue;

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

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FILE_NAME;
   
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
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_GROOVY, URL);
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
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      // check for presence of tab outline
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[index=0]/title"));

      // expand outline tree
      outlineTreeHelper.expandOutlineTree();
      
      // create outline tree map
      outlineTreeHelper.addOutlineItem(0, "TestJSON", 6);
      outlineTreeHelper.addOutlineItem(1, "a1 : java.lang.A", 7);
      outlineTreeHelper.addOutlineItem(2, "a2 : java.lang.A", 7, false);
      outlineTreeHelper.addOutlineItem(3, "a3 : java.lang.A", 7, false);      
      outlineTreeHelper.addOutlineItem(4, "b1 : java.lang.B", 8);
      outlineTreeHelper.addOutlineItem(5, "b2 : java.lang.B", 8, false);
      outlineTreeHelper.addOutlineItem(6, "b3 : String", 8, false);
      outlineTreeHelper.addOutlineItem(7, "getValue1() : java.lang.String", 10); 
      outlineTreeHelper.addOutlineItem(8, "c1 : String", 11);
      outlineTreeHelper.addOutlineItem(9, "identity : Identity", 19);
      outlineTreeHelper.addOutlineItem(10, "c2 : String", 27);
      outlineTreeHelper.addOutlineItem(11, "d : java.lang.String", 31);      

      // TODO update content of node 
      outlineTreeHelper.addOutlineItem(12, "setValue2(@   String) : void", 33);
      outlineTreeHelper.addOutlineItem(13, "printClosureInner", 35);
      outlineTreeHelper.addOutlineItem(14, "printClosureOuter", 40);      
      outlineTreeHelper.addOutlineItem(15, "hello()", 42);
      outlineTreeHelper.addOutlineItem(16, "name4 : String", 44);
      outlineTreeHelper.addOutlineItem(17, "g : String", 47); 

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
