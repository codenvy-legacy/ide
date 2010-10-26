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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 26, 2010 $
 *
 */
public class CodeOutLineRESTTest extends BaseTest
{

   private final static String FILE_NAME = "RESTCodeOutline.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FILE_NAME;

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/RESTCodeOutline.groovy";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL);
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

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
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
      //Open groovy file with content
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      //Open outline panel:
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      //Check ouline tree nodes:
      checkOutlineTree();
      
      //TODO test go to line with outline when parser is ready
   }
   
   private void  checkOutlineTree() throws Exception
   {
      //check for presence of tab outline
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[index=0]/title"));
      
     //check tree correctly created:
      assertEquals("@Path : QueryParam", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("TestService", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[1]");
      //TODO check other nodes of outline tree when parser is ready
   }
}
