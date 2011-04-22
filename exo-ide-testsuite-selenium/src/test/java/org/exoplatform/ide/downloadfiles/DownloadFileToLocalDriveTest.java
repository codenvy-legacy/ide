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
package org.exoplatform.ide.downloadfiles;

import static org.junit.Assert.assertEquals;

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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Test for downloading file to local drive.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 */
public class DownloadFileToLocalDriveTest extends BaseTest
{

   private static final String RANDOM_STRING = UUID.randomUUID().toString();

   private static final String FILE_NAME = String.valueOf(System.currentTimeMillis());

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME 
      + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         //file mime-type is important in this test-case, 
         //because in mimeTypes.rdf (if firefox profile)
         //was recognized actions with text/plain files.
         //They will be downloaded to download dir
         //without asking.
         VirtualFileSystemUtils.put(RANDOM_STRING.getBytes(), MimeType.TEXT_PLAIN, URL + FILE_NAME);
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

   @Ignore
   @Test
   public void testDownloadFileToLocalDrive() throws Exception
   {
      waitForRootElement();

      IDE.NAVIGATION.selectItem(URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.selectItem(URL + FILE_NAME );
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
  
//      //TODO fix download option in menu (see issue 721);(further code is not working)
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD);
      /*
       * File will be downloaded automaticaly.
       */

      Thread.sleep(TestConstants.SLEEP * 3); //wait for download file
      String donwloadPath = System.getProperty("java.io.tmpdir");
      FileInputStream fstream = new FileInputStream(donwloadPath + "/" + FILE_NAME);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String controlStrLine;
      controlStrLine = br.readLine();
      assertEquals(RANDOM_STRING, controlStrLine);

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FILE_NAME);
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

}