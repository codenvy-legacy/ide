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
import org.junit.Ignore;
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

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

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
   @Ignore
   @Test
   public void testDownloadZIPedFoldeToLocalDrive() throws Exception
   {
      {
         waitForRootElement();
         IDE.WORKSPACE.selectItem(
            BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
         IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
         waitForRootElement();

         IDE.WORKSPACE.selectItem(URL);
         IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, true);
         IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER);

         selenium.keyPressNative("10");
         Thread.sleep(TestConstants.SLEEP * 3); //wait for download file
         String donwloadPath = System.getProperty("java.io.tmpdir");
         unzip(donwloadPath + "/" + FOLDER_NAME + ".zip");
         //TODO fix download zip folder option (see issue 721);(further code is not working)
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

   /**
    * @param zipPath
    * unzip file for check inside
    */
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
            copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream("target/"
               + entry.getName())));
         }
         zipFile.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();

      }

   }

   /**
    * check content of file
    * @param in
    * @param out
    * @throws IOException
    */
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
