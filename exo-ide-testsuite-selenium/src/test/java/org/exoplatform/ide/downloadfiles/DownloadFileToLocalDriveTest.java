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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 *
 */
public class DownloadFileToLocalDriveTest extends BaseTest
{
   
   private static final String RANDOM_STRING = UUID.randomUUID().toString();
   
   private static final String FILE_NAME = String.valueOf(System.currentTimeMillis());
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";



   
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.put(RANDOM_STRING.getBytes(), MimeType.APPLICATION_OCTET_STREAM, URL + FILE_NAME);
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
   public void downloadFileToLocalDriveTest() throws Exception
   {
      {
         Thread.sleep(TestConstants.SLEEP);
         selectItemInWorkspaceTree(WS_NAME);
         runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
         Thread.sleep(TestConstants.SLEEP);
         selectItemInWorkspaceTree(FILE_NAME);
         Thread.sleep(TestConstants.SLEEP_SHORT);
         checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);
         runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD);
         Thread.sleep(TestConstants.SLEEP);
         selenium.keyPressNative("10");
         Thread.sleep(TestConstants.SLEEP*3); //wait for download file
         String donwloadPath = System.getProperty("java.io.tmpdir");
         FileInputStream fstream = new FileInputStream(donwloadPath + "/" + FILE_NAME);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String controlStrLine;
         controlStrLine = br.readLine();
         assertEquals(RANDOM_STRING, controlStrLine);
      }

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