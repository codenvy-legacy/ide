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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
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
 * http://jira.exoplatform.com/browse/IDE-412
 * 
 * Create new Netvibes file and save it. Then close and reopen file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class ReopenJustCreatedFileTest extends BaseTest
{
   private static final String FOLDER_NAME = ReopenJustCreatedFileTest.class.getSimpleName() ;

   private static final String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/" + FOLDER_NAME + "/";
   
   private static final String NETVIBES_FILE_NAME = "file-" + ReopenJustCreatedFileTest.class.getSimpleName();
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(STORAGE_URL);
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
         VirtualFileSystemUtils.delete(STORAGE_URL);
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
   public void testReopenJustCreatedFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/");
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      
      String fileName =IDE.EDITOR.getTabTitle(0);
      
      assertEquals("Untitled file.html *", fileName);
      
      saveAsUsingToolbarButton(NETVIBES_FILE_NAME);
      
      Thread.sleep(TestConstants.SLEEP);
      
     IDE.EDITOR.closeTab(0);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(NETVIBES_FILE_NAME, false);
      
      fileName =IDE.EDITOR.getTabTitle(0);
      
      assertEquals(NETVIBES_FILE_NAME, fileName);
   }

}
