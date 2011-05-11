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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeleteSeveralFilesSimultaniouslyTest extends BaseTest
{

   private static String FOLDER_NAME = DeleteSeveralFilesSimultaniouslyTest.class.getSimpleName() + " - " + System.currentTimeMillis();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   /**
    * 
    */
   @BeforeClass
   public static void setUp()
   {
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, 
            WS_URL + FOLDER_NAME + "/in test.html");
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE,
            WS_URL + FOLDER_NAME + "/in test.groovy");
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML, 
            WS_URL + FOLDER_NAME + "/in test.xml");
         
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML,
            WS_URL + FOLDER_NAME + "/" + HTML_FILE_NAME);         
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE,
            WS_URL + FOLDER_NAME + "/" + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML,
            WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * 
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testDeleteSeveralFilesSimultaniously() throws Exception
   {
      waitForRootElement();

      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/" + GROOVY_FILE_NAME);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.NAVIGATION.deleteSelectedItems();      
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME + "/" + GROOVY_FILE_NAME).getStatusCode());
      

      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME).getStatusCode());
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/" + HTML_FILE_NAME);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME + "/" + HTML_FILE_NAME).getStatusCode());
      
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      IDE.NAVIGATION.deleteSelectedItems();
   }

}