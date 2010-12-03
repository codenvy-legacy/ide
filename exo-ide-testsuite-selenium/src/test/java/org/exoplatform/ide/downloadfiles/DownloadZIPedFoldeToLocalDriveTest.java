/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DownloadZIPedFoldeToLocalDriveTest extends BaseTest
{
   private static final String RANDOM_STRING = UUID.randomUUID().toString();

   private static final String RANDOM_STRING_TXT = UUID.randomUUID().toString();

   private static final String FOLDER_NAME = UUID.randomUUID().toString();;

   private static final String FILE_NAME = "EXO" + String.valueOf(System.currentTimeMillis());

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(RANDOM_STRING.getBytes(), MimeType.APPLICATION_OCTET_STREAM, URL + FILE_NAME);
         VirtualFileSystemUtils.put(RANDOM_STRING_TXT.getBytes(), MimeType.TEXT_PLAIN, URL + FILE_NAME + ".txt");
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
   public void testDownloadZIPedFoldeToLocalDrive() throws Exception
   {
      {
         Thread.sleep(TestConstants.SLEEP);
         selectItemInWorkspaceTree(WS_NAME);
         IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
         Thread.sleep(TestConstants.SLEEP);
         selectItemInWorkspaceTree(FOLDER_NAME);
         IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, true);
         IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER);
         Thread.sleep(TestConstants.SLEEP);
         selenium.keyPressNative("10");
         Thread.sleep(TestConstants.SLEEP * 3); //wait for download file
         String donwloadPath = System.getProperty("java.io.tmpdir");
         unzip(donwloadPath + "/" + FOLDER_NAME + ".zip");

         FileInputStream fstream = new FileInputStream("target/" + FOLDER_NAME + "/" + FILE_NAME);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String controlStrLine;
         controlStrLine = br.readLine();
         assertEquals(RANDOM_STRING, controlStrLine);
         
         fstream = new FileInputStream("target/" + FOLDER_NAME + "/" + FILE_NAME + ".txt");
         in = new DataInputStream(fstream);
         br = new BufferedReader(new InputStreamReader(in));
         controlStrLine = br.readLine();
         assertEquals(RANDOM_STRING_TXT, controlStrLine);
      }

   }

   private void unzip(String zipPath)
   {
      ZipFile zipFile;
      try
      {
         zipFile = new ZipFile(zipPath);
         Enumeration entries = zipFile.entries();
         while (entries.hasMoreElements())
         {
            ZipEntry entry = (ZipEntry)entries.nextElement();

            if (entry.isDirectory())
            {
               (new File("target/" + entry.getName())).mkdir();
               continue;
            }
            copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream("target/" + entry
               .getName())));
         }
         zipFile.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();

      }

   }

   private static void copyInputStream(InputStream in, OutputStream out) throws IOException
   {
      byte[] buffer = new byte[1024];
      int len;

      while ((len = in.read(buffer)) >= 0)
         out.write(buffer, 0, len);

      in.close();
      out.close();
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
