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

import static org.junit.Assert.*;

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

import java.io.IOException;

public class DeleteSeveralFilesSimultaniouslyTest extends BaseTest
{
     
   
   private static String FOLDER_NAME = "DeleteSeveralFilesSimultaniously";

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";
   
   private static String CUR_TIME = String.valueOf(System.currentTimeMillis());

   private final static String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   
   @BeforeClass
   public static void setUp()
   {
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, STORAGE_URL + CUR_TIME + ".html");
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, STORAGE_URL + CUR_TIME + ".groovy");
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML, STORAGE_URL + CUR_TIME + ".xml");
         
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, STORAGE_URL + FOLDER_NAME + "/" + HTML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, STORAGE_URL + FOLDER_NAME + "/"  + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML, STORAGE_URL + FOLDER_NAME + "/" + XML_FILE_NAME);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }


   /**
    * 
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteSeveralFilesSimultaniously() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(FOLDER_NAME);  
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(GROOVY_FILE_NAME);
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".groovy");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".html");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".xml");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkToolbarButtonState(ToolbarCommands.File.DELETE, false);
      
            
      selectItemInWorkspaceTree(GROOVY_FILE_NAME);
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(XML_FILE_NAME);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkToolbarButtonState(ToolbarCommands.File.DELETE, true);
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      Thread.sleep(TestConstants.SLEEP_SHORT); 
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
      assertTrue(selenium.isTextPresent("Delete Item(s)"));
      assertTrue(selenium.isTextPresent("Do you want to delete 3 items?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
     
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME + "/" + HTML_FILE_NAME).getStatusCode());
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME + "/" + GROOVY_FILE_NAME).getStatusCode());
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME + "/" + XML_FILE_NAME).getStatusCode());
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isTextPresent(GROOVY_FILE_NAME));
      assertFalse(selenium.isTextPresent(HTML_FILE_NAME));
      assertFalse(selenium.isTextPresent(XML_FILE_NAME));
      
      // ----5----
     
      
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".groovy");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".html");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectItemInWorkspaceTree(CUR_TIME + ".xml");
      selenium.controlKeyUp();
            
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkToolbarButtonState(ToolbarCommands.File.DELETE, true);
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
          
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
      assertTrue(selenium.isTextPresent("Delete Item(s)"));
      assertTrue(selenium.isTextPresent("Do you want to delete 4 items?"));
      
      // ----5----
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME).getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".groovy").getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".xml").getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".html").getStatusCode());
      assertTrue(selenium.isTextPresent(CUR_TIME + ".groovy"));
      assertTrue(selenium.isTextPresent(CUR_TIME + ".html"));
      assertTrue(selenium.isTextPresent(CUR_TIME + ".xml"));
      assertTrue(selenium.isTextPresent(FOLDER_NAME));
      
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
      assertTrue(selenium.isTextPresent("Delete Item(s)"));
      assertTrue(selenium.isTextPresent("Do you want to delete 4 items?"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("scLocator=//Window[ID=\"ideDeleteItemForm\"]/closeButton/");
      
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME).getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".groovy").getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".xml").getStatusCode());
      assertEquals(200,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".html").getStatusCode());
      assertTrue(selenium.isTextPresent(CUR_TIME + ".groovy"));
      assertTrue(selenium.isTextPresent(CUR_TIME + ".html"));
      assertTrue(selenium.isTextPresent(CUR_TIME + ".xml"));
      assertTrue(selenium.isTextPresent(FOLDER_NAME));
      
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
      assertTrue(selenium.isTextPresent("Delete Item(s)"));
      assertTrue(selenium.isTextPresent("Do you want to delete 4 items?"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + FOLDER_NAME).getStatusCode());
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".groovy").getStatusCode());
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".xml").getStatusCode());
      assertEquals(404,VirtualFileSystemUtils.get(STORAGE_URL + CUR_TIME + ".html").getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + ".groovy"));
      assertFalse(selenium.isTextPresent(CUR_TIME + ".html"));
      assertFalse(selenium.isTextPresent(CUR_TIME + ".xml"));
      assertFalse(selenium.isTextPresent(FOLDER_NAME));
      
     }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + CUR_TIME + ".groovy");
         VirtualFileSystemUtils.delete(STORAGE_URL + CUR_TIME + ".xml");
         VirtualFileSystemUtils.delete(STORAGE_URL + CUR_TIME + ".html");
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

 
   
}