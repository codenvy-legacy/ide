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
import org.junit.Test;

/**
 * IDE-11: Deleting files. 
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class DeletingFilesTest extends BaseTest
{

   private static String FOLDER_NAME = DeletingFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String GOOGLE_GADGET_FILE_NAME = "newGoogleGadgetFile.xml";

   private static String JAVA_SCRIPT_FILE_NAME = "newJavaScriptFile.js";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TEXT_FILE_NAME = "newTxtFile.txt";
   
   private static String CUR_TIME = String.valueOf(System.currentTimeMillis());

   private final static String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/"
      + FOLDER_NAME + "/";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   private static String HTML_FILE_URL = STORAGE_URL + CUR_TIME + HTML_FILE_NAME;

   private static String GROOVY_FILE_URL = STORAGE_URL + CUR_TIME + GROOVY_FILE_NAME;

   private static String GOOGLE_GADGET_FILE_URL = STORAGE_URL + CUR_TIME + GOOGLE_GADGET_FILE_NAME;

   private static String JAVA_SCRIPT_FILE_URL = STORAGE_URL + CUR_TIME + JAVA_SCRIPT_FILE_NAME;

   private static String XML_FILE_URL = STORAGE_URL + CUR_TIME + XML_FILE_NAME;

   private static String TEXT_FILE_URL = STORAGE_URL + CUR_TIME + TEXT_FILE_NAME;

   @BeforeClass
   public static void setUp()
   {
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, HTML_FILE_URL);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, GROOVY_FILE_URL);
         VirtualFileSystemUtils.put(PATH + GOOGLE_GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, GOOGLE_GADGET_FILE_URL);
         VirtualFileSystemUtils
            .put(PATH + JAVA_SCRIPT_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, JAVA_SCRIPT_FILE_URL);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML, XML_FILE_URL);
         VirtualFileSystemUtils.put(PATH + TEXT_FILE_NAME, MimeType.TEXT_PLAIN, TEXT_FILE_URL);
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

   //IDE-11: Deleting files.
   @Test
   public void testDeletingFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      
      openOrCloseFolder(FOLDER_NAME);
      
      openFile(CUR_TIME + GROOVY_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(GROOVY_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(GROOVY_FILE_NAME));
      Thread.sleep(TestConstants.SLEEP);

      openFile(CUR_TIME + GOOGLE_GADGET_FILE_NAME);

      // delete selected items by using "Enter" key to verify issue IDE-488 "Keyboard keys are handled incorrect in the "Delete Item(s)" dialog form."
      IDE.toolbar().runCommand(ToolbarCommands.File.DELETE);
      // click "Esc" to close dialog
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.toolbar().runCommand(ToolbarCommands.File.DELETE);     
      // click "Enter" to remove selected items
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(GOOGLE_GADGET_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + GOOGLE_GADGET_FILE_NAME));
      Thread.sleep(TestConstants.SLEEP);

      openFile(CUR_TIME + JAVA_SCRIPT_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(JAVA_SCRIPT_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + JAVA_SCRIPT_FILE_NAME));
      Thread.sleep(TestConstants.SLEEP);

      openFile(CUR_TIME + XML_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(XML_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + XML_FILE_NAME));
      Thread.sleep(TestConstants.SLEEP);
      
      openFile(CUR_TIME + TEXT_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(TEXT_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + TEXT_FILE_NAME));

      Thread.sleep(TestConstants.SLEEP);
      
      openFile(CUR_TIME + HTML_FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(404, VirtualFileSystemUtils.get(HTML_FILE_URL).getStatusCode());
      assertFalse(selenium.isTextPresent(CUR_TIME + HTML_FILE_NAME));
   }

   private void openFile(String fileName) throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(fileName, false);
      Thread.sleep(TestConstants.SLEEP);
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